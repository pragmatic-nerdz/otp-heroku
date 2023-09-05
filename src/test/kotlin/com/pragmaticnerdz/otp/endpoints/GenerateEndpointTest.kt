package com.pragmaticnerdz.otp.endpoints

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
internal class GenerateEndpointTest {
    @Autowired
    private lateinit var otpRepository: OtpRepository

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun generateEmailOtp() {
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
    }
}
