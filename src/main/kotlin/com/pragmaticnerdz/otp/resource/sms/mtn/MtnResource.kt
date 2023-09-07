package com.pragmaticnerdz.otp.resource.sms.mtn

import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import com.pragmaticnerdz.otp.resource.sms.mtn.dto.MtnOutboundSMSMessageRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

class MtnResource(
    private val hostnane: String,
    private val consumerKey: String,
    private val consumerSecret: String,
    private val serviceCode: String,
    private val rest: RestTemplate,
) : SmsSenderResource {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MtnResource::class.java)
    }

    override fun send(uuid: String, address: String, password: String) {
        try {
            val accessToken = login()
            outbound(uuid, address, password, accessToken)
        } catch (ex: Exception) {
            LOGGER.warn("Ignoring SMS error", ex)
        }
    }

    /**
     * See https://developers.mtn.com/products/oauth-v1
     */
    private fun login(): String {
        // Payload
        val data = LinkedMultiValueMap<String, String>()
        data["client_id"] = consumerKey
        data["client_secret"] = consumerSecret

        // Headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        // Post
        val response = rest.postForEntity(
            "https://$hostnane/v1/oauth/access_token?grant_type=client_credentials",
            HttpEntity(data, headers),
            Any::class.java,
        ).body as Map<String, Any>

        return response["access_token"].toString()
    }

    /**
     * https://developers.mtn.com/products/sms-v3-api
     */
    private fun outbound(uuid: String, address: String, password: String, accessToken: String): String {
        // Payload
        val request = MtnOutboundSMSMessageRequest(
            message = password,
            clientCorrelatorId = uuid,
            serviceCode = serviceCode,
            receiverAddress = listOf(address),
        )

        // Headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add("Authorization", "Bearer $accessToken")

        // Post
        val response = rest.postForEntity(
            "https://$hostnane/v3/sms/messages/sms/outbound",
            HttpEntity(request, headers),
            Any::class.java,
        ).body as Map<String, Any>

        return response["transactionId"].toString()
    }
}
