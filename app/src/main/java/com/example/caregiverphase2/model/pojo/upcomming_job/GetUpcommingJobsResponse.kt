package com.example.caregiverphase2.model.pojo.upcomming_job


import com.google.gson.annotations.SerializedName

data class GetUpcommingJobsResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("http_status_code")
    val httpStatusCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: Any
)