package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_agency_profile.GetAgencyProfileResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAgencyProfileRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getAgencyProfile(
        token: String,
        job_id: String
    ): Flow<GetAgencyProfileResponse?> = flow{
        emit(apiInterface.getAgencyProfile(token,job_id))
    }.flowOn(Dispatchers.IO)
}