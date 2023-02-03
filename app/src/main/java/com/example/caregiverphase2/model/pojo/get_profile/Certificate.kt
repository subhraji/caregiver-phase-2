package com.example.caregiverphase2.model.pojo.get_profile

data class Certificate(
    val created_at: String,
    val certificate_or_course: String,
    val end_year: String,
    val id: Int,
    val document: String,
    val start_year: String,
    val updated_at: String,
    val user_id: Int
)