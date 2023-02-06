package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.accept_job.AcceptJobRequest
import com.example.caregiverphase2.model.pojo.accept_job.AcceptJobResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AcceptJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun acceptJob(
        job_id: String,
        token: String
    ): Flow<AcceptJobResponse?> = flow{
        emit(apiInterface.acceptJob(AcceptJobRequest(job_id), token))
    }.flowOn(Dispatchers.IO)
}