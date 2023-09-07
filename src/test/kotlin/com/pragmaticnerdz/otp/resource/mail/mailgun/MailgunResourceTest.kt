package com.pragmaticnerdz.otp.resource.mail.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pragmaticnerdz.otp.AbstractIntegrationTest
import feign.FeignException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.UUID
import kotlin.test.assertEquals

internal class MailgunResourceTest : AbstractIntegrationTest() {
    @Value("\${otp.resources.mail.mailgun.domain}")
    private lateinit var domain: String

    @Autowired
    private lateinit var resource: MailgunResource

    @MockBean
    private lateinit var api: MailgunMessagesApi

    @Test
    fun send() {
        // WHEN
        resource.send(UUID.randomUUID().toString(), "roger.milla@gmail.com", "123456")

        // THEN
        val message = argumentCaptor<Message>()
        verify(api).sendMessage(eq(domain), message.capture())
        assertEquals(setOf("roger.milla@gmail.com"), message.firstValue.to)
        assertEquals("123456", message.firstValue.text)
        assertEquals("Votre mot de passe", message.firstValue.subject)
    }

    @Test
    fun error() {
        // GIVEN
        doThrow(RuntimeException::class).whenever(api).sendMessage(any(), any())

        // WHEN
        assertThrows<RuntimeException> {
            resource.send(UUID.randomUUID().toString(), "roger.milla@gmail.com", "123456")
        }
    }

    @Test
    fun `ignore TooManyRequest error`() =
        ignoreException(
            FeignException.TooManyRequests("{}", mock(), "{}".toByteArray(), emptyMap()),
        )

    @Test
    fun `ignore Forbidden error`() =
        ignoreException(
            FeignException.Forbidden("{}", mock(), "{}".toByteArray(), emptyMap()),
        )

    @Test
    fun `ignore Unauthorized error`() =
        ignoreException(
            FeignException.Unauthorized("{}", mock(), "{}".toByteArray(), emptyMap()),
        )

    private fun ignoreException(ex: FeignException) {
        // GIVEN
        doThrow(ex).whenever(api).sendMessage(any(), any())

        // WHEN
        resource.send(UUID.randomUUID().toString(), "roger.milla@gmail.com", "123456")

        // THEN
        // Nothing
    }
}
