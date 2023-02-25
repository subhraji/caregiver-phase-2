package com.example.caregiverphase2.model.pojo.upload_documents

data class UploadDocumentsResponse(
    val `data`: Data,
    val http_status_code: Int,
    val message: String,
    val success: Boolean,
    val token: String
)