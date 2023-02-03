package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.add_certificate.AddCertificateResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import toMultipartFormString
import javax.inject.Inject

class AddCertificateRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addCertificate(
        document: MultipartBody.Part?,
        certificate_or_course: String,
        start_year: String,
        end_year: String,
        token: String
    ): Flow<AddCertificateResponse?> = flow{
        emit(apiInterface.addCertificate(
            document,
            certificate_or_course.toMultipartFormString(),
            start_year.toMultipartFormString(),
            end_year.toMultipartFormString(),
            token = token
        ))
    }.flowOn(Dispatchers.IO)
}