package com.example.caregiverphase2.model.pojo.forgot_pass_change_pass

data class ChangeForgotPassRequest(
    val email: String,
    val password: String,
    val confirm_password: String,
    val fcm_token: String
)