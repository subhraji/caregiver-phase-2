package com.example.caregiverphase2.model.pojo.chat

data class ChatModel(
    val msgUuid: String,
    val msg: String?,
    val image: String,
    val time: String,
    val isSender: Boolean
){
    var isSeen = false
}