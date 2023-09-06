package com.pragmaticnerdz.otp.resource.mail.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource

class MailgunResource(
    private val api: MailgunMessagesApi,
    private val domain: String,
) : EmailSenderResource {
    override fun send(address: String, password: String) {
        val message = Message.builder()
            .from("pragmatic.nerdz@gmail.com")
            .to(address)
            .subject("Votre mot de passe")
            .text(password)
            .build()
        api.sendMessage(domain, message)
    }
}
