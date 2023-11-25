package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.accept_bidded_job.AcceptBiddedJobRequest
import com.example.caregiverphase2.model.pojo.accept_bidded_job.AcceptBiddedJobResponse
import com.example.caregiverphase2.model.pojo.accept_job.AcceptJobRequest
import com.example.caregiverphase2.model.pojo.accept_job.AcceptJobResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AcceptBiddedJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun acceptBiddedJob(
        job_id: String,
        token: String
    ): Flow<AcceptBiddedJobResponse?> = flow{
        emit(apiInterface.acceptBiddedJob(AcceptBiddedJobRequest(job_id), token))
    }.flowOn(Dispatchers.IO)
}