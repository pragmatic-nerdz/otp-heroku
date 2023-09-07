package com.pragmaticnerdz.otp.resource.mq

import com.pragmaticnerdz.otp.dto.OtpType

data class PasswordGeneratedEvent(
    val type: OtpType = OtpType.EMAIL,
    val uuid: String = "",
    val address: String = "",
    val password: String = "",
)
