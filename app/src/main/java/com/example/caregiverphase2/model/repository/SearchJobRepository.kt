package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.search_job.GetSearchResultResponse
import com.example.caregiverphase2.model.pojo.search_job.SearchJobRequest
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchJobRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun searchJob(
        care_type: String?,
        amount_from: String?,
        amount_to: String?,
        token: String
    ): Flow<GetSearchResultResponse?> = flow{
        emit(apiInterface.searchJob(SearchJobRequest(care_type, amount_from, amount_to), token))
    }.flowOn(Dispatchers.IO)
}