package com.pragmaticnerdz.otp

import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.resource.SenderResource
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource
import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import org.springframework.stereotype.Service

@Service
class SenderConsumer(
    private val emailResource: EmailSenderResource,
    private val smsResource: SmsSenderResource,
) {
    fun send(type: OtpType, address: String, password: String): String? {
        return getSenderResource(type).send(address, password)
    }

    private fun getSenderResource(type: OtpType): SenderResource {
        return when (type) {
            OtpType.EMAIL -> emailResource
            OtpType.SMS -> smsResource
        }
    }
}
