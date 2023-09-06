package com.pragmaticnerdz.otp.resource.sms.mtn

import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MtnConfiguration(
    @Value("\${otp.resources.sms.mtn.hostname}") private val hostname: String,
    @Value("\${otp.resources.sms.mtn.consumer-key}") private val consumerKey: String,
    @Value("\${otp.resources.sms.mtn.consumer-secret}") private val consumerSecret: String,
    @Value("\${otp.resources.sms.mtn.service-code}") private val serviceCode: String,
) {
    @Bean
    fun smsService(): SmsSenderResource =
        MtnResource(hostname, consumerKey, consumerSecret, serviceCode)
}
