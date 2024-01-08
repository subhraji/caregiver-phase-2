package com.example.caregiverphase2.model.pojo.get_open_bid_details


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address")
    val address: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("bidding_end_time")
    val biddingEndTime: String,
    @SerializedName("bidding_start_time")
    val biddingStartTime: String,
    @SerializedName("care_items")
    val careItems: List<CareItem>,
    @SerializedName("care_type")
    val careType: String,
    @SerializedName("check_list")
    val checkList: List<String>,
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("company_photo")
    val companyPhoto: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("expertise")
    val expertise: List<String>,
    @SerializedName("job_id")
    val jobId: Int,
    @SerializedName("job_title")
    val jobTitle: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("long")
    val long: String,
    @SerializedName("medical_history")
    val medicalHistory: List<String>,
    @SerializedName("other_requirements")
    val otherRequirements: List<String>,
    @SerializedName("short_address")
    val shortAddress: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("rewards")
    val rewards: String
)