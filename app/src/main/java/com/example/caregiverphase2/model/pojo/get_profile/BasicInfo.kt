package com.example.caregiverphase2.model.pojo.get_profile

data class BasicInfo(
    val bio: String,
    val care_completed: Int,
    val dob: String,
    val experience: Int,
    val gender: String,
    val phone: String,
    val photo: String,
    val user: User,
    val user_id: Int
)