package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_documents.GetDocumentsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetDocumentsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getDocuments(
        token: String,
    ): Flow<GetDocumentsResponse?> = flow{
        emit(apiInterface.getDocuments(token))
    }.flowOn(Dispatchers.IO)
}