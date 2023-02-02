package com.example.caregiverphase2.model.pojo.add_education

data class AddEducationRequest(
    val school_or_university: String,
    val degree: String,
    val start_year: String,
    val end_year: String
)