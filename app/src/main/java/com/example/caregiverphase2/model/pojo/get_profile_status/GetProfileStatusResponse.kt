package com.example.caregiverphase2.model.pojo.get_profile_status


import com.google.gson.annotations.SerializedName

data class GetProfileStatusResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("http_status_code")
    val httpStatusCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: Any
)