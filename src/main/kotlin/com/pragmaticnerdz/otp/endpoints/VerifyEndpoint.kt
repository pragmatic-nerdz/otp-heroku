package com.pragmaticnerdz.otp.endpoints

import com.pragmaticnerdz.otp.dto.ErrorCode
import com.pragmaticnerdz.otp.dto.VerifyOtpRequest
import com.pragmaticnerdz.otp.dto.VerifyOtpResponse
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class VerifyEndpoint(
    private val otpRepository: OtpRepository,
) {
    @PostMapping("/otp/{otp-uuid}/verify")
    fun verify(
        @PathVariable(name = "otp-uuid") uuid: String,
        @RequestBody request: VerifyOtpRequest,
    ): VerifyOtpResponse {
        val otp = otpRepository.findById(uuid)
        return if (otp.isEmpty) {
            VerifyOtpResponse(success = false, error = ErrorCode.EXPIRED)
        } else if (otp.get().password != request.password) {
            VerifyOtpResponse(success = false, error = ErrorCode.INVALID)
        } else {
            VerifyOtpResponse(success = true)
        }
    }
}
