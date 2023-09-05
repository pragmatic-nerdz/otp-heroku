package com.pragmaticnerdz.otp.resource.mail.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource

class MailgunResource(
    private val api: MailgunMessagesApi,
    private val domain: String,
) : EmailSenderResource {
    override fun send(address: String, password: Int) {
        val message = Message.builder()
            .from("pragmatic.nerdz@gmail.com")
            .to(address)
            .subject("Votre mot de passe")
            .text(toPasswordString(password, 6))
            .build()
        api.sendMessage(domain, message)
    }

    private fun toPasswordString(password: Int, length: Int): String {
        val str = password.toString()
        val length = str.length
        return if (length < 6) {
            "0".repeat(6 - length) + str
        } else {
            str
        }
    }
}
