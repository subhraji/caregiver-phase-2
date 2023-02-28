package com.example.caregiverphase2.model.pojo.get_documents

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
    val lat: String,
    val long: String,
    val name: String,
    val phone: Any,
    val role: Any,
    val status: Int,
    val tuberculosis: List<Tuberculosi>,
    val updated_at: String,
    val w4_form: List<W4Form>
)