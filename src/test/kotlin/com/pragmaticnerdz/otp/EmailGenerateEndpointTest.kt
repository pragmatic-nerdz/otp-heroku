package com.pragmaticnerdz.otp

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import com.mailgun.model.message.MessageResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pragmaticnerdz.otp.dto.GenerateOtpRequest
import com.pragmaticnerdz.otp.dto.GenerateOtpResponse
import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import feign.FeignException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
internal class EmailGenerateEndpointTest {
    @Autowired
    private lateinit var db: OtpRepository

    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var mailgun: MailgunMessagesApi

    private val request = GenerateOtpRequest(
        type = OtpType.EMAIL,
        address = "roger.milla@gmail.com",
    )

    @Test
    fun `generated OTP is set via email`() {
        // WHEN
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        val uuid = response.body!!.otpUuid
        val otp = db.findById(uuid)
        assertTrue(otp.isPresent)

        // Make sure email is sent
        Thread.sleep(5000)
        val message = argumentCaptor<Message>()
        verify(mailgun).sendMessage(any(), message.capture())
        assertEquals("Votre mot de passe", message.firstValue.subject)
        assertEquals(6, message.firstValue.text.length)
        assertTrue(message.firstValue.to.contains(request.address))
    }

    @Test
    fun `password is resent on delivery failure`() {
        // GIVEN
        doThrow(RuntimeException::class)
            .doThrow(java.lang.IllegalStateException::class.java)
            .doReturn(
                MessageResponse.builder()
                    .id("1")
                    .message("OK")
                    .build(),
            )
            .whenever(mailgun).sendMessage(any(), any())

        // WHEN
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        val uuid = response.body!!.otpUuid
        val otp = db.findById(uuid)
        assertTrue(otp.isPresent)

        // Make sure email is sent
        Thread.sleep(60000) // Wait to ensure all event are processed
        val message = argumentCaptor<Message>()
        verify(mailgun, times(3)).sendMessage(any(), message.capture())
    }

    @Test
    fun `password is not resent on Unauthorized error`() =
        testIgnoreError(FeignException.Unauthorized("Error", mock(), "{}".toByteArray(), emptyMap()))

    @Test
    fun `password is not resent on Forbidden error`() =
        testIgnoreError(FeignException.Forbidden("Error", mock(), "{}".toByteArray(), emptyMap()))

    @Test
    fun `password is not resent on TooManyRequest error`() =
        testIgnoreError(FeignException.TooManyRequests("Error", mock(), "{}".toByteArray(), emptyMap()))

    private fun testIgnoreError(ex: FeignException) {
        // GIVEN
        doThrow(ex)
            .doReturn(
                MessageResponse.builder()
                    .id("1")
                    .message("OK")
                    .build(),
            )
            .whenever(mailgun).sendMessage(any(), any())

        // WHEN
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        val uuid = response.body!!.otpUuid
        val otp = db.findById(uuid)
        assertTrue(otp.isPresent)

        // Make sure email is sent
        Thread.sleep(10000)
        val message = argumentCaptor<Message>()
        verify(mailgun, times(1)).sendMessage(any(), message.capture())
    }
}
