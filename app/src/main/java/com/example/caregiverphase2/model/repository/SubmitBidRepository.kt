package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidRequest
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SubmitBidRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun submitBid(
        job_id: String,
        token: String
    ): Flow<SubmitBidResponse?> = flow{
        emit(apiInterface.submitBid(SubmitBidRequest(job_id), token))
    }.flowOn(Dispatchers.IO)
}