package com.example.caregiverphase2.model.pojo.get_flags

data class Data(
    val banned_from_bidding: String,
    val banned_from_quick_call: String,
    val flag_number: Int,
    val flag_reason: String,
    val lift_date_time: String,
    val rewards_loose: Int,
    val start_date_time: String
)