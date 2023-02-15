package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.complete_job_response.CompleteJobRequest
import com.example.caregiverphase2.model.pojo.complete_job_response.CompleteJobResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CompleteJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun completeJob(
        job_id: Int,
        token: String,
    ): Flow<CompleteJobResponse?> = flow{
        emit(apiInterface.completeJob(CompleteJobRequest(job_id),token))
    }.flowOn(Dispatchers.IO)
}