package com.pragmaticnerdz.otp

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "OTP API",
        description = "One Time Password API",
    ),
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
