package com.example.caregiverphase2.model.pojo.get_jobs


import com.google.gson.annotations.SerializedName

data class CareItem(
    @SerializedName("age")
    val age: String,
    @SerializedName("careType")
    val careType: String? = null,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("patient_name")
    val patientName: String
)