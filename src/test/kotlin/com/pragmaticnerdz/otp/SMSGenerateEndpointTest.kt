package com.pragmaticnerdz.otp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pragmaticnerdz.otp.dto.GenerateOtpRequest
import com.pragmaticnerdz.otp.dto.GenerateOtpResponse
import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SMSGenerateEndpointTest {
    @Autowired
    private lateinit var db: OtpRepository

    @MockBean
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var rest: TestRestTemplate

    private val request = GenerateOtpRequest(
        type = OtpType.SMS,
        address = "+237655000000",
    )

    @Test
    fun `generate OTP is sent via SMS`() {
        // Given
        doReturn(
            // authentication
            ResponseEntity.ok(
                mapOf(
                    "access_token" to "3232dfd",
                    "token_type" to "bearer",
                ),
            ),
        )
            .doReturn(
                // Send SMS
                ResponseEntity.ok(
                    mapOf(
                        "statusCode" to "0000",
                        "statusMessage" to "Successful",
                        "transactionId" to "1365478abcz-fdhsdfh54351",
                        "data" to { "status" to "PENDING" },
                    ),
                ),
            )
            .whenever(restTemplate)
            .postForEntity(any<String>(), any(), any<Class<Any>>())

        // WHEN
        val response = rest.postForEntity("/otp", request, GenerateOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val uuid = response.body!!.otpUuid
        val otp = db.findById(uuid)
        assertTrue(otp.isPresent)

        Thread.sleep(5000)
        verify(restTemplate).postForEntity(
            eq("https://api.mtn.com/v1/oauth/access_token?grant_type=client_credentials"),
            any(),
            any<Class<Any>>(),
        )
        verify(restTemplate).postForEntity(
            eq("https://api.mtn.com/v3/sms/messages/sms/outbound"),
            any(),
            any<Class<Any>>(),
        )
    }
}
