package com.pragmaticnerdz.otp.dto

data class PasswordGeneratedEvent(
    val type: OtpType = OtpType.EMAIL,
    val uuid: String = "",
    val address: String = "",
    val password: String = "",
)
