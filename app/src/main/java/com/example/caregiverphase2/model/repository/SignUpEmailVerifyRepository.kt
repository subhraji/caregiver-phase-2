package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationRequest
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SignUpEmailVerifyRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getEmailVerificationOtp(
        email: String,
        otp: String
    ): Flow<SignUpEmailVerificationResponse?> = flow{
        emit(apiInterface.getEmailVerificationOtp(SignUpEmailVerificationRequest(email, otp)))
    }.flowOn(Dispatchers.IO)
}