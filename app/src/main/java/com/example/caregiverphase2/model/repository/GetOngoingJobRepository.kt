package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_ongoing_job.GetOngoingJobResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOngoingJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getOngoingJob(
        token: String,
    ): Flow<GetOngoingJobResponse?> = flow{
        emit(apiInterface.getOngoingJob(token))
    }.flowOn(Dispatchers.IO)
}