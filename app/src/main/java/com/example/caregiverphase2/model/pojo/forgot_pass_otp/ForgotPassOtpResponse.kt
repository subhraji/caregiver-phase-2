package com.example.caregiverphase2.model.pojo.forgot_pass_otp

data class ForgotPassOtpResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)