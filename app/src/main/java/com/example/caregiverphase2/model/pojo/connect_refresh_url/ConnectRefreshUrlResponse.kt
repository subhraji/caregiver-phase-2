package com.example.caregiverphase2.model.pojo.connect_refresh_url

data class ConnectRefreshUrlResponse(
    val `data`: String,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)