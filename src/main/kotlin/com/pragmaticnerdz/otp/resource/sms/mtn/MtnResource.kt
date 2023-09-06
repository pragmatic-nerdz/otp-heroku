package com.pragmaticnerdz.otp.resource.sms.mtn

import com.pragmaticnerdz.otp.resource.sms.SmsSenderResource
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.UUID

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

    override fun send(address: String, password: String): String {
        try {
            val accessToken = login()
            return outbound(address, password, accessToken)
        } catch (ex: Exception) {
            LOGGER.warn("Ignoring SMS error", ex)
            return "-"
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
            HttpEntity<MultiValueMap<String, String>>(data, headers),
            Any::class.java,
        ).body as Map<String, Any>

        return response["access_token"].toString()
    }

    /**
     * https://developers.mtn.com/products/sms-v3-api
     */
    private fun outbound(address: String, password: String, accessToken: String): String {
        // Payload
        val request = LinkedMultiValueMap<String, Any>()
        request["message"] = password
        request["clientCorrelatorId"] = UUID.randomUUID().toString()
        request["serviceCode"] = serviceCode
        request["senderAddress"] = "MTN"
        request["receiverAddress"] = listOf(address)
        request["keyword"] = ""
        request["requestDeliveryReceipt"] = "false"

        // Headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add("Authorization", accessToken)

        // Post
        val response = rest.postForEntity(
            "https://$hostnane/v3/sms/messages/sms/outbound",
            HttpEntity<MultiValueMap<String, Any>>(request, headers),
            Any::class.java,
        ).body as Map<String, Any>

        return response["transactionId"].toString()
    }
}
