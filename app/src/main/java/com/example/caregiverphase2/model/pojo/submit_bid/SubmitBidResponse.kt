package com.example.caregiverphase2.model.pojo.submit_bid

data class SubmitBidResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)