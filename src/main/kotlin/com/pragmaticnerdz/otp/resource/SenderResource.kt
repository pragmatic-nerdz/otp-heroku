package com.pragmaticnerdz.otp.resource

interface SenderResource {
    fun send(uuid: String, address: String, password: String): String
}
