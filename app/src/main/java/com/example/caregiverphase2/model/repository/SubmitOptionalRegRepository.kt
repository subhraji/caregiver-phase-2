package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.register_optional.SubmitOptionalRegRequest
import com.example.caregiverphase2.model.pojo.register_optional.SubmitOptionalRegResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SubmitOptionalRegRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun submitOptionalReg(
        job_type: String? = null,
        experience: String? = null,
        token: String
    ): Flow<SubmitOptionalRegResponse?> = flow{
        emit(apiInterface.submitOptionalReg(SubmitOptionalRegRequest(job_type, experience),token))
    }.flowOn(Dispatchers.IO)
}