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
import feign.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class GenerateEndpointTest {
    companion object {
        @JvmStatic
        fun ignoredFeignException(): List<Arguments> =
            listOf(
                Arguments.of(FeignException.Unauthorized("Error", mock(), "{}".toByteArray(), emptyMap())),
                Arguments.of(FeignException.Forbidden("Error", mock(), "{}".toByteArray(), emptyMap())),
                Arguments.of(FeignException.TooManyRequests("Error", mock(), "{}".toByteArray(), emptyMap())),
            )
    }

    @Autowired
    private lateinit var otpRepository: OtpRepository

    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var mailgun: MailgunMessagesApi

    @Test
    fun `generate email OTP`() {
        // WHEN
        val request = GenerateOtpRequest(
            type = OtpType.EMAIL,
            address = "roger.milla@gmail.com",
        )
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        val uuid = response.body!!.otpUuid
        val otp = otpRepository.findById(uuid)
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
    fun `resend password on Email delivery failure`() {
        // GIVEN
        doThrow(RuntimeException::class)
            .doReturn(
                MessageResponse.builder()
                    .id("1")
                    .message("OK")
                    .build(),
            )
            .whenever(mailgun).sendMessage(any(), any())

        // WHEN
        val request = GenerateOtpRequest(
            type = OtpType.EMAIL,
            address = "roger.milla@gmail.com",
        )
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        val uuid = response.body!!.otpUuid
        val otp = otpRepository.findById(uuid)
        assertTrue(otp.isPresent)

        // Make sure email is sent
        Thread.sleep(14000) // Wait to ensure all event are processed
        val message = argumentCaptor<Message>()
        verify(mailgun, times(2)).sendMessage(any(), message.capture())
    }

    @ParameterizedTest
    @MethodSource("ignoredFeignException")
    fun `ignore password on Email delivery Unauthorized error`(ex: FeignException) {
        // GIVEN
        val req: Request = mock()
        doThrow(ex).whenever(mailgun).sendMessage(any(), any())

        // WHEN
        val request = GenerateOtpRequest(
            type = OtpType.EMAIL,
            address = "roger.milla@gmail.com",
        )
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        val uuid = response.body!!.otpUuid
        val otp = otpRepository.findById(uuid)
        assertTrue(otp.isPresent)

        // Make sure email is sent
        Thread.sleep(15000)
        val message = argumentCaptor<Message>()
        verify(mailgun, times(1)).sendMessage(any(), message.capture())
    }

    @Test
    fun generateSmsOtp() {
        // WHEN
        val request = GenerateOtpRequest(
            type = OtpType.SMS,
            address = "+237655000000",
        )
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
