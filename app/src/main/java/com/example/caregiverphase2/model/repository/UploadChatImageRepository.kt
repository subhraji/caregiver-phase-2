package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.upload_chat_image.UploadChatImageResponse
import com.example.caregiverphase2.model.pojo.upload_documents.UploadDocumentsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import toMultipartFormString
import javax.inject.Inject

class UploadChatImageRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun uploadChatImage(
        image: MultipartBody.Part?,
        sent_by: String,
        token: String
    ): Flow<UploadChatImageResponse?> = flow{
        emit(apiInterface.uploadChatImage(
            image,
            sent_by.toMultipartFormString(),
            token = token
        ))
    }.flowOn(Dispatchers.IO)
}