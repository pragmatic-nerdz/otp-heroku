package com.pragmaticnerdz.otp.endpoints

import com.pragmaticnerdz.otp.dto.ValidateOtpRequest
import com.pragmaticnerdz.otp.dto.ValidateOtpResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ValidateEndpoint {
    @PostMapping("/otp/{otp-uuid}/validate")
    fun validate(
        @PathVariable(name = "otp-uuid") otpUuid: String,
        @RequestBody request: ValidateOtpRequest,
    ): ValidateOtpResponse {
        return ValidateOtpResponse(success = true)
    }
}
