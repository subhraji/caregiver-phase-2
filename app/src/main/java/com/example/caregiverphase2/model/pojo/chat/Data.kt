package com.example.caregiverphase2.model.pojo.chat

data class Data(
    val msg: String,
    val image: String,
    val time: String,
    val messageId: String,
    val targetId: String?,
    val userId: String?,
)