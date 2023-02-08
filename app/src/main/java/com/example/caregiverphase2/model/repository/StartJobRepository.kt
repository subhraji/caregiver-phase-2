package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.start_job.JobStartRequest
import com.example.caregiverphase2.model.pojo.start_job.StartJobResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StartJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun startJob(
        job_id: Int,
        token: String,
    ): Flow<StartJobResponse?> = flow{
        emit(apiInterface.startJob(JobStartRequest(job_id),token))
    }.flowOn(Dispatchers.IO)
}