package starter.testtaskjustai.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class InputMessage(
    val groupId: Int,
    val type: String,
    val eventId: String,
    val v: String,
    @JsonProperty("object") val body: Body
)


data class Body(val message: MessageBody, @JsonProperty("client_info") val clientInfo: ClientInfo)

data class MessageBody(
    val date: Int, val fromId: Int, val id: Int, val out: Int, val version: Int, val attachments: List<String>,
    val conversationMessageId: Int, val fwdMessages: List<String>?, val important: Boolean, val isHidden: Boolean,
    val peerId: Int, val randomId: Int, val text: String
)

data class ClientInfo(
    @JsonProperty("button_actions") val buttonActions: List<String>,
    val keyboard: Boolean,
    val inlineKeyboard: Boolean,
    val carousel: Boolean,
    val langId: Int
)