package com.pragmaticnerdz.otp

import com.pragmaticnerdz.otp.dto.ErrorCode
import com.pragmaticnerdz.otp.dto.VerifyOtpRequest
import com.pragmaticnerdz.otp.dto.VerifyOtpResponse
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
@Tag(name = "verify")
class VerifyEndpoint(
    private val db: OtpRepository,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(VerifyEndpoint::class.java)
    }

    @PostMapping("/otp/{otp-uuid}/verify")
    @Operation(
        method = "verify",
        summary = "Verify an OTP",
        description = "Check if a temporary password is valid",
    )
    fun verify(
        @PathVariable(name = "otp-uuid") uuid: String,
        @RequestBody request: VerifyOtpRequest,
    ): ResponseEntity<VerifyOtpResponse> {
        val otp = db.findById(uuid)

        val response = if (otp.isEmpty) {
            VerifyOtpResponse(success = false, error = ErrorCode.EXPIRED)
        } else if (otp.get().password != request.password) {
            VerifyOtpResponse(success = false, error = ErrorCode.INVALID)
        } else {
            VerifyOtpResponse(success = true)
        }

        log(uuid, request, response)
        return toResponseEntity(response)
    }

    private fun toResponseEntity(response: VerifyOtpResponse): ResponseEntity<VerifyOtpResponse> =
        if (response.success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body(response)
        }

    private fun log(uuid: String, request: VerifyOtpRequest, response: VerifyOtpResponse) {
        LOGGER.info("endpoint=/otp/$uuid/verify request_password=${request.password} response_success=${response.success} response_error=${response.error}")
    }
}
