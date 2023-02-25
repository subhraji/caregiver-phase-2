package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.change_profile_pic.ChangeProfilePicResponse
import com.example.caregiverphase2.model.pojo.register.RegisterResponse
import com.example.caregiverphase2.model.pojo.upload_documents.UploadDocumentsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import toMultipartFormString
import javax.inject.Inject

class UploadDocumentsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun uploadDocuments(
        document: MultipartBody.Part?,
        documentCategory: String,
        expiry_date: String,
        token: String
    ): Flow<UploadDocumentsResponse?> = flow{
        emit(apiInterface.uploadDocuments(
            document,
            documentCategory.toMultipartFormString(),
            expiry_date.toMultipartFormString(),
            token = token
        ))
    }.flowOn(Dispatchers.IO)
}