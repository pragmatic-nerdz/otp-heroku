package com.pragmaticnerdz.otp

import com.fasterxml.jackson.databind.ObjectMapper
import com.pragmaticnerdz.otp.dto.GenerateOtpRequest
import com.pragmaticnerdz.otp.dto.GenerateOtpResponse
import com.pragmaticnerdz.otp.dto.PasswordGeneratedEvent
import com.pragmaticnerdz.otp.resource.mq.rabbitmq.RabbitMQConfiguration
import com.pragmaticnerdz.otp.resource.persistence.OtpEntity
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping
@Tag(name = "generate")
class GenerateEndpoint(
    private val db: OtpRepository,
    private val mq: RabbitTemplate,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GenerateEndpoint::class.java)
    }

    @PostMapping("/otp")
    @Operation(
        method = "generate",
        summary = "Generate an OTP",
        description = "Generate a temporary password and send it via email or SMS",
    )
    fun generate(@RequestBody request: GenerateOtpRequest): GenerateOtpResponse {
        val otp = generateOtp()
        passwordGenerated(request, otp)

        val response = GenerateOtpResponse(otpUuid = otp.uuid)
        log(request, response)
        return response
    }

    private fun generateOtp() =
        db.save(
            OtpEntity(
                uuid = UUID.randomUUID().toString(),
                password = (1000000 * Math.random()).toInt(),
            ),
        )

    private fun passwordGenerated(request: GenerateOtpRequest, otp: OtpEntity) {
        val event = PasswordGeneratedEvent(
            type = request.type,
            uuid = otp.uuid,
            address = request.address,
            password = formatPassword(otp.password, 6),
        )
        mq.convertAndSend(
            RabbitMQConfiguration.QUEUE,
            ObjectMapper().writeValueAsString(event),
        )
    }

    private fun formatPassword(password: Int, maxLength: Int): String {
        val str = password.toString()
        val length = str.length
        return if (length < maxLength) {
            "0".repeat(maxLength - length) + str
        } else {
            str
        }
    }

    private fun log(request: GenerateOtpRequest, response: GenerateOtpResponse) {
        LOGGER.info("request_address=${request.address} request_type=${request.type} response_otp_uuid=${response.otpUuid}")
    }
}
