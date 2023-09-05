package com.pragmaticnerdz.otp.service

import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.resource.mail.MailResource
import org.springframework.stereotype.Service

@Service
class SenderService(
    private val mailResource: MailResource,
) {
    companion object {
        const val QUEUE = "otp-queue"
    }

    fun send(type: OtpType, address: String, password: Int) {
        mailResource.send(address, password)
    }
}
