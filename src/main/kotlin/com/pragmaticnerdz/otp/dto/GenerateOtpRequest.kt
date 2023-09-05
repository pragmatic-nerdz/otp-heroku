package com.pragmaticnerdz.otp.dto

data class GenerateOtpRequest(
    val type: OtpType,
    val address: String,
)
