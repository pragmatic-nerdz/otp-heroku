package com.pragmaticnerdz.otp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pragmaticnerdz.otp.dto.GenerateOtpRequest
import com.pragmaticnerdz.otp.dto.GenerateOtpResponse
import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

internal class EmailGenerateEndpointTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var db: OtpRepository

    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var emailSenderResource: EmailSenderResource

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
        val password = argumentCaptor<String>()
        verify(emailSenderResource).send(eq(otp.get().uuid), eq(request.address), password.capture())
        assertEquals(otp.get().password, password.firstValue.toInt())
    }

    @Test
    fun `password is resent on delivery failure`() {
        // GIVEN
        doThrow(RuntimeException::class)
            .doNothing()
            .whenever(emailSenderResource).send(any(), any(), any())

        // WHEN
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        val uuid = response.body!!.otpUuid
        val otp = db.findById(uuid)
        assertTrue(otp.isPresent)

        // Make sure email is sent
        Thread.sleep(15000) // Wait to ensure all event are processed
        val password = argumentCaptor<String>()
        verify(emailSenderResource, times(2)).send(eq(otp.get().uuid), eq(request.address), password.capture())
        assertEquals(otp.get().password, password.firstValue.toInt())
        assertEquals(otp.get().password, password.secondValue.toInt())
    }
}
