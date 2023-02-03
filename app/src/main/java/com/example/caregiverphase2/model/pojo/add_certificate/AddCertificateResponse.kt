package com.example.caregiverphase2.model.pojo.add_certificate


import com.google.gson.annotations.SerializedName

data class AddCertificateResponse(
    @SerializedName("data")
    val `data`: Any,
    @SerializedName("http_status_code")
    val httpStatusCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: Any
)