package com.example.caregiverphase2.model.pojo.chat

data class GetChatResponse(
    val chatModel: List<ChatModel>,
    val httpStatusCode: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)