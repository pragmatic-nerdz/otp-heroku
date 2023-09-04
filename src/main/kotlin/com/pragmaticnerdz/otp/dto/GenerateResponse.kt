package com.pragmaticnerdz.otp.dto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.UUID

data class GenerateResponse(
    val otpUuid: String = "",
    val errorCode: ErrorCode = ErrorCode.NONE,
)
