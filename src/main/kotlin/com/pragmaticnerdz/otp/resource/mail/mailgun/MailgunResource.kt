package com.pragmaticnerdz.otp.resource.mail.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource
import feign.FeignException
import org.slf4j.LoggerFactory

class MailgunResource(
    private val api: MailgunMessagesApi,
    private val domain: String,
) : EmailSenderResource {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailgunResource::class.java)
    }

    override fun send(address: String, password: String) {
        try {
            val message = Message.builder()
                .from("pragmatic.nerdz@gmail.com")
                .to(address)
                .subject("Votre mot de passe")
                .text(password)
                .build()
            api.sendMessage(domain, message)
        } catch (ex: FeignException.TooManyRequests) {
            LOGGER.warn("Email server no longer available", ex)
        } catch (ex: FeignException.Unauthorized) {
            LOGGER.warn("Authentication error", ex)
        }
    }
}
