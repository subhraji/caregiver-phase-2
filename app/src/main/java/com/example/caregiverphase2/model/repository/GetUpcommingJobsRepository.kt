package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.upcomming_job.GetUpcommingJobsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUpcommingJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getUpcommingJobs(
        token: String,
        job_id: Int
    ): Flow<GetUpcommingJobsResponse?> = flow{
        emit(apiInterface.getUpcommingJobs(token, job_id))
    }.flowOn(Dispatchers.IO)
}