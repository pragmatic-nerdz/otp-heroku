package com.pragmaticnerdz.otp.endpoints

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class GenerateEndpointTest {
    @Autowired
    private lateinit var otpRepository: OtpRepository

    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var mailgun: MailgunMessagesApi

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

        // Make sure email is sent
        Thread.sleep(5000)
        val message = argumentCaptor<Message>()
        verify(mailgun).sendMessage(any(), message.capture())
        assertEquals("Votre mot de passe", message.firstValue.subject)
        assertEquals(6, message.firstValue.text.length)
        assertTrue(message.firstValue.to.contains(request.address))
    }
}
