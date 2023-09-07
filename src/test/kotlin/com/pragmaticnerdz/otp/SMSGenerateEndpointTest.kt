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
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

internal class SMSGenerateEndpointTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var db: OtpRepository

    @MockBean
    private lateinit var smsSenderResource: SmsSenderResource

    @Autowired
    private lateinit var rest: TestRestTemplate

    private val request = GenerateOtpRequest(
        type = OtpType.SMS,
        address = "+237655000000",
    )

    @Test
    fun `generate OTP is sent via SMS`() {
        // WHEN
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val uuid = response.body!!.otpUuid
        val otp = db.findById(uuid)
        assertTrue(otp.isPresent)

        Thread.sleep(5000)
        val password = argumentCaptor<String>()
        verify(smsSenderResource).send(eq(otp.get().uuid), eq(request.address), password.capture())
        assertEquals(otp.get().password, password.firstValue.toInt())
    }

    @Test
    fun `password is resent on delivery failure`() {
        // GIVEN
        doThrow(RuntimeException::class)
            .doNothing()
            .whenever(smsSenderResource).send(any(), any(), any())

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
        verify(smsSenderResource, times(2)).send(eq(otp.get().uuid), eq(request.address), password.capture())
        assertEquals(otp.get().password, password.firstValue.toInt())
        assertEquals(otp.get().password, password.secondValue.toInt())
    }
}
