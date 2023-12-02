package com.example.caregiverphase2.model.pojo.reject_bidded_job

data class RejectBiddedJobResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)