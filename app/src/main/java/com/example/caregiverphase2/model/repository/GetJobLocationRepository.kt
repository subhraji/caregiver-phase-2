package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_job_locations.GetJobLocationResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetJobLocationRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getJobLocation(
        token: String,
        current_lat: String,
        current_long: String
    ): Flow<GetJobLocationResponse?> = flow{
        emit(apiInterface.getJobLocation(
            token,
            current_lat,
            current_long
        ))
    }.flowOn(Dispatchers.IO)
}