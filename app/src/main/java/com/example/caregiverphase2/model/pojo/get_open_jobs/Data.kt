package com.example.caregiverphase2.model.pojo.get_open_jobs

data class Data(
    val address: String,
    val amount: String,
    val care_items: List<CareItem>,
    val care_type: String,
    val check_list: List<Any>,
    val company_name: String,
    val company_photo: String,
    val created_at: String,
    val date: String,
    val description: String,
    val end_time: String,
    val experties: List<Any>,
    val job_id: Int,
    val job_title: String,
    val medical_history: List<String>,
    val other_requirements: List<Any>,
    val start_time: String,
    val status: String
)