package com.example.caregiverphase2.model.pojo.search_job

data class CareItem(
    val age: String,
    val careType: String? = null,
    val gender: String,
    val patient_name: String
)