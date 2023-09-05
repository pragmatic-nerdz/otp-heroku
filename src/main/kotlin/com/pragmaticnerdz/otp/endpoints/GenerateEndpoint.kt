package com.pragmaticnerdz.otp.endpoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.pragmaticnerdz.otp.dto.GenerateOtpRequest
import com.pragmaticnerdz.otp.dto.GenerateOtpResponse
import com.pragmaticnerdz.otp.resource.mq.PasswordGeneratedEvent
import com.pragmaticnerdz.otp.resource.mq.rabbitmq.RabbitMQConfiguration
import com.pragmaticnerdz.otp.resource.persistence.OtpEntity
import com.pragmaticnerdz.otp.resource.persistence.OtpRepository
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping
class GenerateEndpoint(
    private val otpRepository: OtpRepository,
    private val rabbit: RabbitTemplate,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GenerateEndpoint::class.java)
    }

    @PostMapping("/otp")
    fun generate(@RequestBody request: GenerateOtpRequest): GenerateOtpResponse {
        val otp = generateOtp()
        passwordGenerated(request, otp)

        val response = GenerateOtpResponse(otpUuid = otp.uuid)
        log(request, response)
        return response
    }

    private fun generateOtp() =
        otpRepository.save(
            OtpEntity(
                uuid = UUID.randomUUID().toString(),
                password = (1000000 * Math.random()).toInt(),
            ),
        )

    private fun passwordGenerated(request: GenerateOtpRequest, otp: OtpEntity) {
        val event = PasswordGeneratedEvent(
            type = request.type,
            address = request.address,
            password = otp.password,
        )
        rabbit.convertAndSend(
            RabbitMQConfiguration.QUEUE,
            ObjectMapper().writeValueAsString(event),
        )
    }

    private fun log(request: GenerateOtpRequest, response: GenerateOtpResponse) {
        LOGGER.info("endpoint=/opt request_address=${request.address} request_type=${request.type} response_opt_uuid=${response.otpUuid}")
    }
}
