package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_complete_job.GetCompleteJobsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCompleteJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getCompleteJob(
        token: String,
    ): Flow<GetCompleteJobsResponse?> = flow{
        emit(apiInterface.getCompleteJobs(token))
    }.flowOn(Dispatchers.IO)
}