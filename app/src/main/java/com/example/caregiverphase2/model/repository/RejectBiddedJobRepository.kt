package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.reject_bidded_job.RejectBiddedJobRequest
import com.example.caregiverphase2.model.pojo.reject_bidded_job.RejectBiddedJobResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RejectBiddedJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun rejectBiddedJob(
        job_id: String,
        token: String
    ): Flow<RejectBiddedJobResponse?> = flow{
        emit(apiInterface.rejectBiddedJob(RejectBiddedJobRequest(job_id), token))
    }.flowOn(Dispatchers.IO)
}