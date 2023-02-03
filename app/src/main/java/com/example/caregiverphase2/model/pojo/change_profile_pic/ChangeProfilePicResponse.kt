package com.example.caregiverphase2.model.pojo.change_profile_pic


import com.google.gson.annotations.SerializedName

data class ChangeProfilePicResponse(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("http_status_code")
    val httpStatusCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: Any
)