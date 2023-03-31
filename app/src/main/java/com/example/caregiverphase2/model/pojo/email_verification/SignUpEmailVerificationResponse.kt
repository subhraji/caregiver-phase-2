package com.example.caregiverphase2.model.pojo.email_verification

data class SignUpEmailVerificationResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: String
)