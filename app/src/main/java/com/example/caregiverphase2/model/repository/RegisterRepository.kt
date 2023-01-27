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
        full_address: String,
        short_address: String,
        token: String
    ): Flow<RegisterResponse?> = flow{
        emit(apiInterface.registration(
            photo,
            phone.toMultipartFormString(),
            dob.toMultipartFormString(),
            gender.toMultipartFormString(),
            ssn.toMultipartFormString(),
            full_address.toMultipartFormString(),
            short_address.toMultipartFormString(),
            token = token
        ))
    }.flowOn(Dispatchers.IO)
}