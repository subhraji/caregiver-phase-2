package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_bidded_jobs.GetBiddedJobsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetBiddedJobsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getBiddedJobs(
        token: String,
        id: Int
    ): Flow<GetBiddedJobsResponse?> = flow{
        emit(apiInterface.getBiddedJobs(token, id))
    }.flowOn(Dispatchers.IO)
}