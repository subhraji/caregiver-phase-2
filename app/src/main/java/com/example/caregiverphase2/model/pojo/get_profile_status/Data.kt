package com.example.caregiverphase2.model.pojo.get_profile_status


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_basic_info_added")
    val isBasicInfoAdded: Int,
    @SerializedName("is_documents_uploaded")
    val isDocumentsUploaded: Int,
    @SerializedName("is_optional_info_added")
    val isOptionalInfoAdded: Int,
    @SerializedName("is_profile_approved")
    val isProfileApproved: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int
)