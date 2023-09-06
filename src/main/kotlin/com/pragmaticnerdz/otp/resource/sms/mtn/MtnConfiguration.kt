package com.pragmaticnerdz.otp.resource.sms.mtn

import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MtnConfiguration {
    @Bean
    fun smsSenderResource(): SmsSenderResource =
        MtnResource()
}
