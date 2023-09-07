package com.pragmaticnerdz.otp.resource.sms.mtn.dto

data class MtnOutboundSMSMessageRequest(
    val message: String,
    val clientCorrelatorId: String,
    val serviceCode: String,
    val senderAddress: String = "",
    val receiverAddress: List<String>,
    val keyword: String = "",
    val requestDeliveryReceipt: Boolean = false,
)
