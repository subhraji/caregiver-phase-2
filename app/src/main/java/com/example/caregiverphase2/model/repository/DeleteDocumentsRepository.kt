package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.delete_document.DeleteDocumentRequest
import com.example.caregiverphase2.model.pojo.delete_document.DeleteDocumentResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteDocumentsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun deleteDocument(
        documentCategory: String,
        document_id: Int,
        token: String,
    ): Flow<DeleteDocumentResponse?> = flow{
        emit(apiInterface.deleteDocument(DeleteDocumentRequest(documentCategory, document_id),token))
    }.flowOn(Dispatchers.IO)
}