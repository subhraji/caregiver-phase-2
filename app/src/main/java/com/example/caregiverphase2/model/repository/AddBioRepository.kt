package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.add_bio.AddBioRequest
import com.example.caregiverphase2.model.pojo.add_bio.AddBioResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddBioRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addBio(
        bio: String,
        token: String
    ): Flow<AddBioResponse?> = flow{
        emit(apiInterface.addBio(AddBioRequest(bio), token))
    }.flowOn(Dispatchers.IO)
}