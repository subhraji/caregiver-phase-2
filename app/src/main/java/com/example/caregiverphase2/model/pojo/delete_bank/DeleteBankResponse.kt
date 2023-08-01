package com.example.caregiverphase2.model.pojo.delete_bank

data class DeleteBankResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)