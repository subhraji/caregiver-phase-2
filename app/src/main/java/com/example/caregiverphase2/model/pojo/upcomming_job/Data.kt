package com.example.caregiverphase2.model.pojo.upcomming_job


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("agency_name")
    val agencyName: String,
    @SerializedName("agency_photo")
    val agencyPhoto: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("care_items")
    val careItems: List<CareItem>,
    @SerializedName("care_type")
    val careType: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("job_starts_in")
    val jobStartsIn: Int,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("long")
    val long: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("status")
    val status: Any,
    @SerializedName("title")
    val title: String,
    val job_id: Int,
    val agency_address: String,
    val description: String?,
    @SerializedName("experties")
    val experties: List<String>,
    @SerializedName("medical_history")
    val medicalHistory: List<String>,
    @SerializedName("other_requirements")
    val otherRequirements: List<String>,
    @SerializedName("check_list")
    val checkList: List<String>,
)