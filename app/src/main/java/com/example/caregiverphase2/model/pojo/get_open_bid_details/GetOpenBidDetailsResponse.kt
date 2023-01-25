package com.example.caregiverphase2.model.pojo.get_open_bid_details


import com.google.gson.annotations.SerializedName

data class GetOpenBidDetailsResponse(
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