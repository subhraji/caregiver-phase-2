package com.example.caregiverphase2.model.pojo.email_verification

data class SignUpEmailVerificationResponse(
    val `data`: Int,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: String,
    val verified_user_id: String
)