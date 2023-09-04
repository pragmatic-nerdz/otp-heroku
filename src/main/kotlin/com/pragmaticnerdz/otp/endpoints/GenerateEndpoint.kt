package com.pragmaticnerdz.otp.endpoints

import com.pragmaticnerdz.otp.dto.GenerateResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping
class GenerateEndpoint {
    @PostMapping("/otp")
    fun generate(): GenerateResponse {
        return GenerateResponse(otpUuid = UUID.randomUUID().toString())
    }
}
