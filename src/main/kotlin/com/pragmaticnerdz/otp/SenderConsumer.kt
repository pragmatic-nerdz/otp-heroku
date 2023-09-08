package com.pragmaticnerdz.otp

import com.pragmaticnerdz.otp.dto.OtpType
import com.pragmaticnerdz.otp.dto.PasswordGeneratedEvent
import com.pragmaticnerdz.otp.resource.SenderResource
import com.pragmaticnerdz.otp.resource.mail.EmailSenderResource
import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SenderConsumer(
    private val emailResource: EmailSenderResource,
    private val smsResource: SmsSenderResource,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SenderConsumer::class.java)
    }

    fun send(event: PasswordGeneratedEvent) {
        try {
            val messageId = getSenderResource(event.type).send(event.uuid, event.address, event.password)
            log(event, messageId)
        } catch (ex: Exception) {
            logAndThrow(event, ex)
        }
    }

    private fun getSenderResource(type: OtpType): SenderResource {
        return when (type) {
            OtpType.EMAIL -> emailResource
            OtpType.SMS -> smsResource
        }
    }

    private fun log(event: PasswordGeneratedEvent, messageId: String) {
        LOGGER.info("event_uuid=${event.uuid} event_type=${event.type} event_address=${event.address} event_password=${event.password} message_id=$messageId")
    }

    private fun logAndThrow(event: PasswordGeneratedEvent, ex: Exception) {
        LOGGER.error(
            "event_uuid=${event.uuid} event_type=${event.type} event_address=${event.address} event_password=${event.password}",
            ex,
        )
        throw ex
    }
}
