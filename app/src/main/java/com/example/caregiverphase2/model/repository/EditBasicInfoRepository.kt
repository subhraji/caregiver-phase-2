package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.edit_basic_info.EditBasicInfoRequest
import com.example.caregiverphase2.model.pojo.edit_basic_info.EditBasicInfoResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class EditBasicInfoRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun editBasicInfo(
        phone: String,
        experience: String,
        token: String
    ): Flow<EditBasicInfoResponse?> = flow{
        emit(apiInterface.editBasicInfo(EditBasicInfoRequest(phone, experience), token))
    }.flowOn(Dispatchers.IO)
}