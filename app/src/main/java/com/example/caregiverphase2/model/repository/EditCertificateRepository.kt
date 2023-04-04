package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.add_certificate.AddCertificateResponse
import com.example.caregiverphase2.model.pojo.edit_certificate.EditCertificateResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import toMultipartFormString
import javax.inject.Inject

class EditCertificateRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun editCertificate(
        document: MultipartBody.Part? = null,
        certificate_or_course: String? = null,
        start_year: String? = null,
        end_year: String? = null,
        cart_id: String,
        token: String
    ): Flow<EditCertificateResponse?> = flow{
        emit(apiInterface.editCertificate(
            document,
            certificate_or_course?.toMultipartFormString(),
            start_year?.toMultipartFormString(),
            end_year?.toMultipartFormString(),
            cart_id.toMultipartFormString(),
            token = token
        ))
    }.flowOn(Dispatchers.IO)
}