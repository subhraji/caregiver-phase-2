package com.example.caregiverphase2.model.pojo.get_open_jobs

data class GetOpenJobsResponse(
    val `data`: List<Data>,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)