package com.example.caregiverphase2.model.pojo.edit_education

data class EditEducationRequest(
    val school_or_university: String,
    val degree: String,
    val start_year: String,
    val end_year: String,
    val edu_id: String
)