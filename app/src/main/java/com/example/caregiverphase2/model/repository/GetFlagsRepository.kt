package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_flags.GetFlagsReposnse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetFlagsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getFlags(
        token: String,
    ): Flow<GetFlagsReposnse?> = flow{
        emit(apiInterface.getFlags(token))
    }.flowOn(Dispatchers.IO)
}