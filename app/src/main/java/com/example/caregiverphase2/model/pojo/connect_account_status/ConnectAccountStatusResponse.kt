package com.example.caregiverphase2.model.pojo.connect_account_status

data class ConnectAccountStatusResponse(
    val `data`: Data?,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)