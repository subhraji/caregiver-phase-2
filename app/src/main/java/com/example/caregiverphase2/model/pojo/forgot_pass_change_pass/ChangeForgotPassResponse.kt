package com.example.caregiverphase2.model.pojo.forgot_pass_change_pass

data class ChangeForgotPassResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)