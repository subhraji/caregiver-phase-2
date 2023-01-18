package com.example.caregiverphase2.model.pojo.get_jobs


import com.google.gson.annotations.SerializedName

data class GetJobsResponse(
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