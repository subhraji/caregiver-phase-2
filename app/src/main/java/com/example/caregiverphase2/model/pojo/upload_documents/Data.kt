package com.example.caregiverphase2.model.pojo.upload_documents

data class Data(
    val child_abuse: List<ChildAbuse>,
    val covid: List<Covid>,
    val created_at: String,
    val criminal: List<Criminal>,
    val deleted_at: Any,
    val driving: List<Driving>,
    val email: String,
    val email_verified_at: Any,
    val employment: List<Employment>,
    val id: Int,
    val identification: List<Identification>,
    val lat: Any,
    val long: Any,
    val name: String,
    val phone: Any,
    val role: Any,
    val status: Int,
    val tuberculosis: List<Tuberculosi>,
    val updated_at: String,
    val w4_form: List<W4Form>
)