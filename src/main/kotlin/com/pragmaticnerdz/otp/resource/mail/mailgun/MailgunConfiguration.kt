package com.pragmaticnerdz.otp.resource.mail.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import com.pragmaticnerdz.otp.resource.mail.MailResource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress

@Configuration
class MailgunConfiguration(
    @Value("\${otp.resources.mail.mailgun.api-key}") private val apiKey: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailgunMailResource::class.java)
    }

    @Bean
    fun mailgunApi(): MailgunMessagesApi =
        MailgunClient
            .config(apiKey)
            .createApi(MailgunMessagesApi::class.java)

    @Bean
    fun mailService(): MailResource {
        val domain = getDomainName()
        LOGGER.info("Initializing MailService with domain=$domain")

        return MailgunMailResource(mailgunApi(), domain)
    }

    private fun getDomainName(): String =
        InetAddress.getLocalHost().hostName
}
