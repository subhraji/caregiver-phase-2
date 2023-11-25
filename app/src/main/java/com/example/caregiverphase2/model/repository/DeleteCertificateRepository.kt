package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.delete_certificate.DeleteCertificateRequest
import com.example.caregiverphase2.model.pojo.delete_certificate.DeleteCertificateResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteCertificateRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun deleteCertificate(
        id: String,
        token: String,
    ): Flow<DeleteCertificateResponse?> = flow{
        emit(apiInterface.deleteCertificate(DeleteCertificateRequest(id),token))
    }.flowOn(Dispatchers.IO)
}