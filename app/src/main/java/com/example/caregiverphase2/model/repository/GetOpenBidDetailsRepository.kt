package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_open_bid_details.GetOpenBidDetailsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOpenBidDetailsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getOpenBidDetails(
        job_id: Int,
        token: String
    ): Flow<GetOpenBidDetailsResponse?> = flow{
        emit(apiInterface.getOpenBidDetails(token, job_id))
    }.flowOn(Dispatchers.IO)
}