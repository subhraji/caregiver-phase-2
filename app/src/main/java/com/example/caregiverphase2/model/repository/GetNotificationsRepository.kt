package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_notifications.GetNotificationsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetNotificationsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getNotifications(
        token: String,
    ): Flow<GetNotificationsResponse?> = flow{
        emit(apiInterface.getNotifications(
            token
        ))
    }.flowOn(Dispatchers.IO)
}