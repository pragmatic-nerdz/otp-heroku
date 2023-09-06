package com.pragmaticnerdz.otp.resource.mail.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource
import org.springframework.amqp.rabbit.connection.NodeLocator.LOGGER
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

@Configuration
class MailgunConfiguration(
    @Value("\${otp.resources.mail.mailgun.api-key}") private val apiKey: String,
    @Value("\${otp.server-url}") private val serverUrl: String,
) {
    @Bean
    fun mailgunApi(): MailgunMessagesApi =
        MailgunClient
            .config(apiKey)
            .createApi(MailgunMessagesApi::class.java)

    @Bean
    fun mailService(): EmailSenderResource {
        val domain = URI(serverUrl).host
        LOGGER.info("Initializing MailService with domain=$domain")

        return MailgunResource(mailgunApi(), domain)
    }
}
