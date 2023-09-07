package com.pragmaticnerdz.otp.resource.sms.mtn

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pragmaticnerdz.otp.AbstractIntegrationTest
import com.pragmaticnerdz.otp.resource.sms.mtn.dto.MtnOutboundSMSMessageRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.UUID

internal class MtnResourceTest : AbstractIntegrationTest() {
    @Value("\${otp.resources.sms.mtn.hostname}")
    private lateinit var hostname: String

    @Value("\${otp.resources.sms.mtn.consumer-key}")
    private lateinit var consumerKey: String

    @Value("\${otp.resources.sms.mtn.consumer-secret}")
    private lateinit var consumerSecret: String

    @Value("\${otp.resources.sms.mtn.service-code}")
    private lateinit var serviceCode: String

    @Autowired
    private lateinit var resource: MtnResource

    @MockBean
    private lateinit var rest: RestTemplate

    @Test
    fun send() {
        // GIVEN
        val uuid = UUID.randomUUID().toString()
        val accessToken = UUID.randomUUID().toString()
        doReturn(
            // Authentication
            ResponseEntity.ok(
                mapOf(
                    "access_token" to accessToken,
                    "token_type" to "bearer",
                ),
            ),
        )
            .doReturn(
                // Send
                ResponseEntity.ok(
                    mapOf(
                        "statusCode" to "0000",
                        "statusMessage" to "Successful",
                        "transactionId" to "1365478abcz-fdhsdfh54351",
                        "data" to { "status" to "PENDING" },
                    ),
                ),
            )
            .whenever(rest)
            .postForEntity(any<String>(), any(), any<Class<Any>>())

        // WHEN
        resource.send(uuid, "+23797000000", "123456")

        // THEN
        val url = argumentCaptor<String>()
        val entity = argumentCaptor<HttpEntity<Any>>()
        verify(rest, times(2)).postForEntity(
            url.capture(),
            entity.capture(),
            any<Class<Any>>(),
        )

        val authUrl = url.firstValue
        val authEntity = entity.firstValue as HttpEntity<LinkedMultiValueMap<String, String>>
        assertEquals("https://$hostname/v1/oauth/access_token?grant_type=client_credentials", authUrl)
        assertEquals(listOf(consumerKey), authEntity.body?.get("client_id"))
        assertEquals(listOf(consumerSecret), authEntity.body?.get("client_secret"))
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, authEntity.headers.contentType)

        val sendUrl = url.secondValue
        val sendEntity = entity.secondValue as HttpEntity<MtnOutboundSMSMessageRequest>
        assertEquals("https://$hostname/v3/sms/messages/sms/outbound", sendUrl)
        assertEquals(serviceCode, sendEntity.body?.serviceCode)
        assertEquals("123456", sendEntity.body.message)
        assertEquals(listOf("+23797000000"), sendEntity.body?.receiverAddress)
        assertEquals(uuid, sendEntity.body?.clientCorrelatorId)
        assertEquals(MediaType.APPLICATION_JSON, sendEntity.headers.contentType)
        assertEquals(listOf("Bearer $accessToken"), sendEntity.headers["Authorization"])
    }

    @Test
    fun `ignore errors`() {
        // GIVEN
        doThrow(RuntimeException::class).whenever(rest).postForEntity(any<String>(), any(), any<Class<*>>())

        // WHEN
        resource.send(UUID.randomUUID().toString(), "+23797000000", "123456")

        // THEN
        // Nothing
    }
}
