package com.example.caregiverphase2.model.pojo.resend_otp

data class ResendOtpResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)