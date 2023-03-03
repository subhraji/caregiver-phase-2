package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_reg_details.GetRegistrationDetailsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetRegistrationDetailsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getRegDetails(
        token: String
    ): Flow<GetRegistrationDetailsResponse?> = flow{
        emit(apiInterface.getRegDetails(token))
    }.flowOn(Dispatchers.IO)
}