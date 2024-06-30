package starter.testtaskjustai

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import starter.testtaskjustai.dto.Body
import starter.testtaskjustai.dto.ConfirmationCodeDto
import starter.testtaskjustai.dto.InputMessage
import starter.testtaskjustai.dto.ServerConfigDto
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import kotlin.random.Random


@RestController
class ApiController {
    private val client: HttpClient = HttpClient.newHttpClient()
    @Value("\${bot.token}")
    private val token: String = ""
    @Value("\${bot.groupId}")
    private val groupId: Int = 0
    private val apiVersion = "5.199"
    private val vkUrl = "https://api.vk.ru/method/"
    private val messageSendMethod = "messages.send"
    private val callbackConfirmationCodeMethod = "groups.getCallbackConfirmationCode"
    private val addCallbackServerMethod = "groups.addCallbackServer"

    companion object {
        const val MESSAGE_NEW_TYPE = "message_new"
        const val CONFIRMATION_TYPE = "confirmation"
    }

    @PostMapping("/")
    fun newMessage(
        @RequestBody message: Map<String, Any>
    ): String? {
        println(message)
        val messageType = message["type"]
        when (messageType) {
            MESSAGE_NEW_TYPE -> sendEchoMessage(message["object"] as String)
            CONFIRMATION_TYPE -> return sendConfirmationString()
        }
        return "ok"
    }

    private fun sendConfirmationString(): String {
        val requestBody: String = ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(
                mapOf(
                    "group_id" to groupId,
                    "v" to apiVersion
                )
            )
        var request =
            HttpRequest.newBuilder(URI(vkUrl + callbackConfirmationCodeMethod))
                .header("Authorization", "Bearer $token")
                .POST(BodyPublishers.ofString(requestBody))
                .build()
        var response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val confirmationCodeDto = ObjectMapper().readValue(response.body(), ConfirmationCodeDto::class.java)
        return confirmationCodeDto.response.code
    }

    private fun sendEchoMessage(message: String) {
        val body = ObjectMapper().readValue(message, Body::class.java)
        val text = body.message.text
        val fromId = body.message.peerId
        val random = Random.nextInt()
        val outputMessage = "Вы сказали: $text"


        val requestBody: String = ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(
                mapOf(
                    "message" to outputMessage,
                    "peer_id" to fromId,
                    "random_id" to random,
                    "v" to apiVersion
                )
            )
        var request =
            HttpRequest.newBuilder(URI(vkUrl + messageSendMethod))
                .setHeader("Authorization", "Bearer $token")
                .POST(BodyPublishers.ofString(requestBody))
                .build()
        client.send(request, HttpResponse.BodyHandlers.ofString())

    }

    @PostMapping("/startNewServer")
    fun sutUpServer(
        @RequestBody serverConfigDto: ServerConfigDto
    ) {
        val requestBody: String = ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(
                mapOf(
                    "group_id" to groupId,
                    "url" to serverConfigDto.url,
                    "title" to serverConfigDto.title,
                    "v" to apiVersion
                )
            )
        var request =
            HttpRequest.newBuilder(URI(vkUrl + addCallbackServerMethod))
                .setHeader("Authorization", "Bearer $token")
                .POST(BodyPublishers.ofString(requestBody))
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("Server id: " + response.body())
    }
}