package com.pragmaticnerdz.otp.dto

data class VerifyOtpResponse(
    val success: Boolean = false,
    val error: ErrorCode = ErrorCode.NONE,
)
