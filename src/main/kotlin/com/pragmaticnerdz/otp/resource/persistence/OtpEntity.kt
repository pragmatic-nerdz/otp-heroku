package com.pragmaticnerdz.otp.resource.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(timeToLive = 300000L)
data class OtpEntity(
    @Id
    val uuid: String,
    val password: Int,
)
