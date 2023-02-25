package com.example.caregiverphase2.model.pojo.upload_documents

data class Identification(
    val created_at: String,
    val expiry_date: Any,
    val id: Int,
    val image: String,
    val name: String,
    val status: Int,
    val type: String,
    val updated_at: String,
    val user_id: Int
)