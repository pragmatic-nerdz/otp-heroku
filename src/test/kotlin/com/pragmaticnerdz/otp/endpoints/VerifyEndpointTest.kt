package com.pragmaticnerdz.otp.endpoints

import com.pragmaticnerdz.otp.dto.ErrorCode
import com.pragmaticnerdz.otp.dto.VerifyOtpRequest
import com.pragmaticnerdz.otp.dto.VerifyOtpResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class VerifyEndpointTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun success() {
        // WHEN
        val uuid = UUID.randomUUID().toString()
        val request = VerifyOtpRequest(password = "123243")
        val response = rest.postForEntity("/otp/$uuid/verify", request, VerifyOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        assertTrue(response.body!!.success)
        assertEquals(ErrorCode.NONE, response.body!!.error)
    }
}
