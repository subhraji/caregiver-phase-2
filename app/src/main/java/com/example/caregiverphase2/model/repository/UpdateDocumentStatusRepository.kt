package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.update_doc_status.UpdateDocStatusResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateDocumentStatusRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun updateDocumentStatus(
        token: String
    ): Flow<UpdateDocStatusResponse?> = flow{
        emit(apiInterface.updateDocStatus(token))
    }.flowOn(Dispatchers.IO)
}