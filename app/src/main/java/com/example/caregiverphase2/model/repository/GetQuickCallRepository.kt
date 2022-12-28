package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOPenJobsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetQuickCallRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getQuickCall(
        token: String,
    ): Flow<GetOPenJobsResponse?> = flow{
        emit(apiInterface.getQuickCall(token))
    }.flowOn(Dispatchers.IO)
}