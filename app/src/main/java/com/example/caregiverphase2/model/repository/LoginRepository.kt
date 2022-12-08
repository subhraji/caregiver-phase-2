package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.login.LoginRequest
import com.example.caregiverphase2.model.pojo.login.LoginResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun login(
        email: String,
        password: String
    ): Flow<LoginResponse?> = flow{
        emit(apiInterface.login(LoginRequest(email,password)))
    }.flowOn(Dispatchers.IO)
}