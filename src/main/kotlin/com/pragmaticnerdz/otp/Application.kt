package com.pragmaticnerdz.otp

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
