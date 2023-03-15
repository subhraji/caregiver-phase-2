package com.example.caregiverphase2.model.pojo.get_complete_job

data class Data(
    val address: String,
    val agency_address: String,
    val agency_name: String,
    val agency_photo: String,
    val amount: String,
    val care_items: List<CareItem>,
    val care_type: String,
    val check_list: List<String>,
    val date: String,
    val description: String,
    val end_time: String,
    val expertise: List<String>,
    val job_id: Int,
    val lat: String,
    val long: String,
    val medical_history: List<String>,
    val other_requirements: List<String>,
    val short_address: String,
    val start_time: String,
    val status: String,
    val title: String
)