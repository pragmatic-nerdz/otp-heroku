package com.pragmaticnerdz.otp.dto

data class GenerateOtpRequest(
    val type: OtpType = OtpType.EMAIL,
    val address: String = "",
)
