package com.example.caregiverphase2.model.pojo.add_review

data class AddReviewRequest(
    val job_id: String,
    val rating: String,
    val review: String
)