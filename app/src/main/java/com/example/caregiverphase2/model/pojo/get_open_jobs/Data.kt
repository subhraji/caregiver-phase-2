package com.example.caregiverphase2.model.pojo.get_open_jobs

data class Data(
    val address: String,
    val amount: String,
    val care_items: List<CareItem>,
    val care_type: String,
    val check_list: List<String>,
    val company_name: String,
    val company_photo: String,
    val created_at: String,
    val start_date: String,
    val end_date: String,
    val description: String,
    val end_time: String,
    val expertise: List<String>,
    val job_id: Int,
    val job_title: String,
    val lat: String,
    val long: String,
    val medical_history: List<String>,
    val other_requirements: List<String>,
    val short_address: String,
    val start_time: String,
    val status: String,
    val distance: String?
)