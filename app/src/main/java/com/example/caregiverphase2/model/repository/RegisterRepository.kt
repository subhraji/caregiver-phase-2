package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.register.RegisterResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import toMultipartFormString
import javax.inject.Inject

class RegisterRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun register(
        photo: MultipartBody.Part?,
        phone: String,
        dob: String,
        gender: String,
        ssn: String,
        experience: String,
        job_type: String,
        street: String,
        city_or_district: String,
        state: String,
        zip_code: String,
        education: String? = null,
        certificate: String? = null,
        token: String
    ): Flow<RegisterResponse?> = flow{
        emit(apiInterface.registration(
            photo,
            phone.toMultipartFormString(),
            dob.toMultipartFormString(),
            gender.toMultipartFormString(),
            ssn.toMultipartFormString(),
            experience.toMultipartFormString(),
            job_type.toMultipartFormString(),
            street.toMultipartFormString(),
            city_or_district.toMultipartFormString(),
            state.toMultipartFormString(),
            zip_code.toMultipartFormString(),
            education?.toMultipartFormString(),
            certificate?.toMultipartFormString(),
            token
        ))
    }.flowOn(Dispatchers.IO)
}