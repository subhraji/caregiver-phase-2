package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOpenJobsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOpenBidsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getOpenBids(
        token: String,
        page: Int? = null
    ): Flow<GetOpenJobsResponse?> = flow{
        emit(apiInterface.getOPenBids(token, page))
    }.flowOn(Dispatchers.IO)
}