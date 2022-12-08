package com.example.caregiverphase2.model.pojo.login

data class LoginResponse(
    val `data`: Any,
    val http_status_code: String,
    val message: String,
    val success: Boolean,
    val token: String?
)