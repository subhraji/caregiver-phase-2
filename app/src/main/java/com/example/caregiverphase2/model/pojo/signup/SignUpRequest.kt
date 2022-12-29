package com.example.caregiverphase2.model.pojo.signup

data class SignUpRequest(
    val otp: Int,
    val name: String,
    val email: String,
    val password: String,
    val confirm_password: String
)