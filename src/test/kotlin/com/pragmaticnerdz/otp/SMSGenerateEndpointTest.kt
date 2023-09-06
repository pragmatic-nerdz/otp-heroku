package com.pragmaticnerdz.otp

import com.pragmaticnerdz.otp.dto.GenerateOtpRequest
import com.pragmaticnerdz.otp.dto.GenerateOtpResponse
import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SMSGenerateEndpointTest {
    @Autowired
    private lateinit var db: OtpRepository

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
    }
}
