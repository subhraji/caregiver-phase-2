package com.example.caregiverphase2.model.pojo.get_profile

data class ProfileCompletionStatus(
    val is_basic_info_added: Int,
    val is_optional_info_added: Int,
    val is_documents_uploaded: Int,
    val is_profile_approved: Int,
)