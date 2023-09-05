package com.pragmaticnerdz.otp.service

import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.resource.SenderResource
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource
import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import org.springframework.stereotype.Service

@Service
class SenderService(
    private val emailResource: EmailSenderResource,
    private val smsResource: SmsSenderResource,
) {
    fun send(type: OtpType, address: String, password: Int) {
        getSenderResource(type).send(address, password)
    }

    private fun getSenderResource(type: OtpType): SenderResource =
        when (type) {
            OtpType.EMAIL -> emailResource
            OtpType.SMS -> smsResource
        }
}
