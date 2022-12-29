package com.example.caregiverphase2.model.pojo.change_password

data class ChangePasswordResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)