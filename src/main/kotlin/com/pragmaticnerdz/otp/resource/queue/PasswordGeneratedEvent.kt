package com.pragmaticnerdz.otp.resource.queue

import com.pragmaticnerdz.otp.dto.OtpType

data class PasswordGeneratedEvent(
    val type: OtpType = OtpType.EMAIL,
    val address: String = "",
    val password: Int = 0,
)
