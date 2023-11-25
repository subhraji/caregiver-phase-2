package com.example.caregiverphase2.model.pojo.get_flags

data class GetFlagsReposnse(
    val `data`: List<Data>,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)