package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.resend_otp.ResendOtpRequest
import com.example.caregiverphase2.model.pojo.resend_otp.ResendOtpResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResendOtpRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun resendOtp(
        email: String,
    ): Flow<ResendOtpResponse?> = flow{
        emit(apiInterface.resendOtp(ResendOtpRequest(email)))
    }.flowOn(Dispatchers.IO)
}