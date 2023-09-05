package com.pragmaticnerdz.otp.resource

interface SenderResource {
    fun send(address: String, password: Int)
}
