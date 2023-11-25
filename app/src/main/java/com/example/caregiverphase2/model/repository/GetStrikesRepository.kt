package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_strikes.GetStrikesResonse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetStrikesRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getStrikes(
        token: String
    ): Flow<GetStrikesResonse?> = flow{
        emit(apiInterface.getStrikes(token))
    }.flowOn(Dispatchers.IO)
}