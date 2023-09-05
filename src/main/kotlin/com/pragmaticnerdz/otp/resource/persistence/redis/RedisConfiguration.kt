package com.pragmaticnerdz.otp.resource.persistence.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.net.URI

/**
 * See https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/
 */
@Configuration
class RedisConfiguration(
    @Value("\${otp.resources.persistence.redis.url}") private val url: String,
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val address = URI(url)
        return LettuceConnectionFactory(RedisStandaloneConfiguration(address.host, address.port))
    }
}
