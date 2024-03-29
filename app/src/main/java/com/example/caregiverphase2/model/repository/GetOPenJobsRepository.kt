package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_jobs.GetJobsResponse
import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOpenJobsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOPenJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getOPenJobs(
        token: String,
        id: Int? = null,
        page: Int? = null
    ): Flow<GetJobsResponse?> = flow{
        emit(apiInterface.getOpenJobs(token, id, page))
    }.flowOn(Dispatchers.IO)
}