package com.example.caregiverphase2.model.pojo.signup

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirm_password: String,
    val fcm_token: String
)