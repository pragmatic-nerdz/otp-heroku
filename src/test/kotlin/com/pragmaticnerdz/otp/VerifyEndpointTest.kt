package com.pragmaticnerdz.otp

import com.pragmaticnerdz.otp.dto.ErrorCode
import com.pragmaticnerdz.otp.dto.VerifyOtpRequest
import com.pragmaticnerdz.otp.dto.VerifyOtpResponse
import com.pragmaticnerdz.otp.resource.persistence.OtpEntity
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.util.UUID
import kotlin.test.assertFalse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class VerifyEndpointTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var otpRepository: OtpRepository

    @Test
    fun success() {
        // GIVEN
        val uuid = UUID.randomUUID().toString()
        val password = 123
        otpRepository.save(OtpEntity(uuid, password))

        // WHEN
        val request = VerifyOtpRequest(password = password)
        val response = rest.postForEntity("/otp/$uuid/verify", request, VerifyOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertTrue(response.body!!.success)
        assertEquals(ErrorCode.NONE, response.body!!.error)
    }

    @Test
    fun invalid() {
        // GIVEN
        val uuid = UUID.randomUUID().toString()
        val password = 123
        otpRepository.save(OtpEntity(uuid, password))

        // WHEN
        val request = VerifyOtpRequest(password = 55555)
        val response = rest.postForEntity("/otp/$uuid/verify", request, VerifyOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertFalse(response.body!!.success)
        assertEquals(ErrorCode.INVALID, response.body!!.error)
    }

    @Test
    fun expired() {
        // WHEN
        val uuid = UUID.randomUUID().toString()
        val request = VerifyOtpRequest(password = 123)
        val response = rest.postForEntity("/otp/$uuid/verify", request, VerifyOtpResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // Make sure the OTP has been saved
        assertFalse(response.body!!.success)
        assertEquals(ErrorCode.EXPIRED, response.body!!.error)
    }
}
