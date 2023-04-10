package com.example.caregiverphase2.model.pojo.search_job

data class GetSearchResultResponse(
    val `data`: List<Data>,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)