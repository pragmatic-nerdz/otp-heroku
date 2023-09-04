package com.pragmaticnerdz.otp.dto

data class GenerateResponse(
    val otpUuid: String = "",
    val errorCode: ErrorCode = ErrorCode.NONE,
)
