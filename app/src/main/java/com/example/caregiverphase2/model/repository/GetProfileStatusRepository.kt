package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_profile_status.GetProfileStatusResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetProfileStatusRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getProfileStatus(
        token: String,
    ): Flow<GetProfileStatusResponse?> = flow{
        emit(apiInterface.getProfileStatus(token))
    }.flowOn(Dispatchers.IO)
}