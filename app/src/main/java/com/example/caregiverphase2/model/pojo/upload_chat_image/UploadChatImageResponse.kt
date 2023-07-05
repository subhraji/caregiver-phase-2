package com.example.caregiverphase2.model.pojo.upload_chat_image

data class UploadChatImageResponse(
    val `data`: String,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)