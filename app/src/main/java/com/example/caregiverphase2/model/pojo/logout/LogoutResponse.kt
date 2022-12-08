package com.example.caregiverphase2.model.pojo.logout

data class LogoutResponse(
    val `data`: Any,
    val http_status_code: String,
    val message: String,
    val success: Boolean,
    val token: Any
)