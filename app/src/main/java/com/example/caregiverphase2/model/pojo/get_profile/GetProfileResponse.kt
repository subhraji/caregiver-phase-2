package com.example.caregiverphase2.model.pojo.get_profile

data class GetProfileResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)