package com.pragmaticnerdz.otp.resource.persistence.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
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
    fun redisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory(redisConfiguration())

    private fun redisConfiguration(): org.springframework.data.redis.connection.RedisConfiguration {
        val address = URI(url)
        val config = RedisStandaloneConfiguration(address.host, address.port)

        val userInfo = address.userInfo
        if (userInfo != null) {
            val i = userInfo.indexOf(':')
            config.username = userInfo.substring(0, i)
            config.password = RedisPassword.of(userInfo.substring(i + 1))
        }
        return config
    }
}
