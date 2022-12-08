package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.logout.LogoutResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LogoutRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun logout(
        token: String
    ): Flow<LogoutResponse?> = flow{
        emit(apiInterface.logout(token))
    }.flowOn(Dispatchers.IO)
}