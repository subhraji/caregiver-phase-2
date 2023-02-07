package com.example.caregiverphase2.model.pojo.upcomming_job


import com.google.gson.annotations.SerializedName

data class CareItem(
    @SerializedName("age")
    val age: String,
    @SerializedName("careType")
    val careType: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("patient_name")
    val patientName: String
)