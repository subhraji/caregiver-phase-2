package com.example.caregiverphase2.model.pojo.add_bank

data class AddBankResponse(
    val `data`: String,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)