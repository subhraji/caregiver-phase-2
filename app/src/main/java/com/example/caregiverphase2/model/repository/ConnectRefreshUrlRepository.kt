package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.connect_refresh_url.ConnectRefreshUrlResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConnectRefreshUrlRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun connectRefreshUrl(
        token: String
    ): Flow<ConnectRefreshUrlResponse?> = flow{
        emit(apiInterface.connectRefreshUrl(token))
    }.flowOn(Dispatchers.IO)
}