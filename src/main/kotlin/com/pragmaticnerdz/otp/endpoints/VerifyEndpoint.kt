package com.pragmaticnerdz.otp.endpoints

import com.pragmaticnerdz.otp.dto.VerifyOtpRequest
import com.pragmaticnerdz.otp.dto.VerifyOtpResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class VerifyEndpoint {
    @PostMapping("/otp/{otp-uuid}/verify")
    fun verify(
        @PathVariable(name = "otp-uuid") uuid: String,
        @RequestBody request: VerifyOtpRequest,
    ): VerifyOtpResponse {
        return VerifyOtpResponse(success = true)
    }
}
