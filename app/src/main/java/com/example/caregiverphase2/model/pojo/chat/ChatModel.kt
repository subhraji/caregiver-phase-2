package com.example.caregiverphase2.model.pojo.chat

data class ChatModel(
    val image: String,
    val is_message_seen: Int,
    val messageId: String,
    val msg: String?,
    val targetId: String?,
    val userId: String?,
    val time: String
){
    var isSender = false
    var isSeen = false
}