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
        street: String,
        city: String,
        state: String,
        zipcode: String,
        appartment_or_unit: String? = null,
        floor_no: String? = null,
        country: String,
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
            street.toMultipartFormString(),
            city.toMultipartFormString(),
            state.toMultipartFormString(),
            zipcode.toMultipartFormString(),
            appartment_or_unit!!.toMultipartFormString(),
            floor_no!!.toMultipartFormString(),
            country.toMultipartFormString(),
            token = token
        ))
    }.flowOn(Dispatchers.IO)
}