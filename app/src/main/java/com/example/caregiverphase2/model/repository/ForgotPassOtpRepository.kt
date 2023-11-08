package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.forgot_pass_otp.ForgotPassOtpRequest
import com.example.caregiverphase2.model.pojo.forgot_pass_otp.ForgotPassOtpResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotPassOtpRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun forgotPassOtp(
        email: String,
        otp: String,
    ): Flow<ForgotPassOtpResponse?> = flow{
        emit(apiInterface.forgotPassOtp(ForgotPassOtpRequest(email, otp)))
    }.flowOn(Dispatchers.IO)
}