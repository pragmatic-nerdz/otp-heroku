package com.pragmaticnerdz.otp.resource.mq.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.pragmaticnerdz.otp.Sender
import com.pragmaticnerdz.otp.resource.mq.PasswordGeneratedEvent
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

/**
 * See https://docs.spring.io/spring-amqp/reference/html/
 */
@Configuration
class RabbitMQConfiguration(
    @Value("\${otp.resources.queue.rabbitmq.url}") private val url: String,
    private val sender: Sender,
) {
    companion object {
        const val QUEUE = "otp-queue"
    }

    @Bean
    fun connectionFactory(): CachingConnectionFactory =
        CachingConnectionFactory(URI(url))

    @Bean
    fun amqpAdmin(): RabbitAdmin =
        RabbitAdmin(connectionFactory())

    @Bean
    fun rabbitTemplate(): RabbitTemplate =
        RabbitTemplate(connectionFactory())

    @Bean
    fun senderQueue(): Queue =
        Queue(QUEUE)

    @RabbitListener(queues = [QUEUE])
    fun onPasswordGenerated(payload: String) {
        val mapper = ObjectMapper()
        val event = mapper.readValue(payload, PasswordGeneratedEvent::class.java)
        sender.send(event.type, event.address, event.password)
    }
}
