package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.change_profile_pic.ChangeProfilePicResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class ChangeProfilePicRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun changeProfilePic(
        document: MultipartBody.Part?,
        token: String
    ): Flow<ChangeProfilePicResponse?> = flow{
        emit(apiInterface.changeProfilePic(
            document,
            token = token
        ))
    }.flowOn(Dispatchers.IO)
}