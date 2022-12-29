package com.example.caregiverphase2.model.pojo.change_password

data class ChangePasswordRequest(
    val current_password: String,
    val password: String,
    val confirm_password: String
)