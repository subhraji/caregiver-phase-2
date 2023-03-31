package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.signup.SignUpRequest
import com.example.caregiverphase2.model.pojo.signup.SignUpResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SignUpRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun signup(
        name: String,
        email: String,
        password: String,
        con_password: String,
        fcm_token: String
    ): Flow<SignUpResponse?> = flow{
        emit(apiInterface.signup(SignUpRequest(name,email,password,con_password,fcm_token)))
    }.flowOn(Dispatchers.IO)
}