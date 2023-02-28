package com.example.caregiverphase2.model.pojo.get_documents

data class W4Form(
    val created_at: String,
    val expiry_date: String,
    val id: Int,
    val image: String,
    val name: String,
    val status: Int,
    val type: String,
    val updated_at: String,
    val user_id: Int
)