package com.pragmaticnerdz.otp.resource.mail.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MailgunConfiguration(
    @Value("\${otp.resources.mail.mailgun.api-key}") private val apiKey: String,
    @Value("\${otp.resources.mail.mailgun.domain}") private val domain: String,
) {
    @Bean
    fun mailgunApi(): MailgunMessagesApi =
        MailgunClient
            .config(apiKey)
            .createApi(MailgunMessagesApi::class.java)

    @Bean
    fun emailSenderResource(): EmailSenderResource =
        MailgunResource(mailgunApi(), domain)
}
