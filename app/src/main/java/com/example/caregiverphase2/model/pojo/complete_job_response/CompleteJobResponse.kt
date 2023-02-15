package com.example.caregiverphase2.model.pojo.complete_job_response

data class CompleteJobResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)