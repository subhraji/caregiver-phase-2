package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.chat.GetChatResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllChatRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getAllChat(
        token: String,
        job_id: Int,
        page: Int
    ): Flow<GetChatResponse?> = flow{
        emit(apiInterface.getAllChat(token,job_id,page))
    }.flowOn(Dispatchers.IO)
}