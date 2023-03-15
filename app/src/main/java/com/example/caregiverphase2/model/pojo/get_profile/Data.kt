package com.example.caregiverphase2.model.pojo.get_profile

data class Data(
    val basic_info: BasicInfo,
    val certificate: List<Certificate>,
    val education: List<Education>,
    val flags: Int,
    val rewards: Int,
    val strikes: Int
)