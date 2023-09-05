package com.pragmaticnerdz.otp.endpoints

import com.pragmaticnerdz.otp.dto.GenerateOtpRequest
import com.pragmaticnerdz.otp.dto.GenerateOtpResponse
import com.pragmaticnerdz.otp.resource.persistence.OtpEntity
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping
class GenerateEndpoint(
    private val otpRepository: OtpRepository,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GenerateEndpoint::class.java)
    }

    @PostMapping("/otp")
    fun generate(@RequestBody request: GenerateOtpRequest): GenerateOtpResponse {
        val otp = generateOtp()
        otpRepository.save(otp)

        val response = GenerateOtpResponse(otpUuid = otp.uuid)
        log(request, response)
        return response
    }

    private fun generateOtp() = OtpEntity(
        uuid = UUID.randomUUID().toString(),
        password = (1000000 * Math.random()).toInt(),
    )

    private fun log(request: GenerateOtpRequest, response: GenerateOtpResponse) {
        LOGGER.info("endpoint=/opt request_address=${request.address} request_type=${request.type} response_opt_uuid=${response.otpUuid}")
    }
}
