package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.connect_account_status.ConnectAccountStatusResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConnectAccountRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun connectAccountStatus(
        token: String
    ): Flow<ConnectAccountStatusResponse?> = flow{
        emit(apiInterface.connectAccountStatus(token))
    }.flowOn(Dispatchers.IO)
}