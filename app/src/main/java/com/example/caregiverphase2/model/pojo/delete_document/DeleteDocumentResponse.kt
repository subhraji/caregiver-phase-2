package com.example.caregiverphase2.model.pojo.delete_document

data class DeleteDocumentResponse(
    val `data`: Any,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: Any
)