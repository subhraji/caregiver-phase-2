package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.mark_read_notification.MarkReadNotificationRequest
import com.example.caregiverphase2.model.pojo.mark_read_notification.MarkReadNotificationResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MarkReadNotificationRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun login(
        notification_id: String,
        token: String
    ): Flow<MarkReadNotificationResponse?> = flow{
        emit(apiInterface.markReadNotification(MarkReadNotificationRequest(notification_id), token))
    }.flowOn(Dispatchers.IO)
}