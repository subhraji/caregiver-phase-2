package com.example.caregiverphase2.model.pojo.get_open_jobs

data class Data(
    val address: String,
    val amount: String,
    val care_items: List<CareItem>,
    val care_type: String,
    val check_list: List<Any>,
    val created_at: String,
    val date: String,
    val deleted_at: Any,
    val description: String,
    val end_time: String,
    val experties: List<String>,
    val id: Int,
    val medical_history: List<String>,
    val other_requirements: List<String>,
    val start_time: String,
    val status: String,
    val title: String,
    val updated_at: String,
    val user_id: Int
)