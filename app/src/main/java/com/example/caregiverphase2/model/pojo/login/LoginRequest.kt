package com.example.caregiverphase2.model.pojo.login

data class LoginRequest(
    val email: String,
    val password: String,
    val fcm_token: String
)