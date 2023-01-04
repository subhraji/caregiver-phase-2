package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOPenJobsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOPenJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getOPenJobs(
        token: String,
        id: Int? = null
    ): Flow<GetOPenJobsResponse?> = flow{
        emit(apiInterface.getOpenJobs(token, id))
    }.flowOn(Dispatchers.IO)
}