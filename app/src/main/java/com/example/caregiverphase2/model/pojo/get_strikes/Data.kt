package com.example.caregiverphase2.model.pojo.get_strikes

data class Data(
    val banned_from_bidding: String,
    val banned_from_quick_call: String,
    val lift_date_time: String,
    val rewards_loose: Int,
    val start_date_time: String,
    val strike_number: Int,
    val strike_reason: String
)