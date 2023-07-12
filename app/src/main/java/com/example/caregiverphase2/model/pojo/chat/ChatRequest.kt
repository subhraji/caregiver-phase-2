package com.example.caregiverphase2.model.pojo.chat

data class ChatRequest(
    val msg: String?,
    val userId: String,
    val targetId: String,
    val time: String,
    val image: String,
    val messageId: String,
    val jobId: String,
    val token: String
)