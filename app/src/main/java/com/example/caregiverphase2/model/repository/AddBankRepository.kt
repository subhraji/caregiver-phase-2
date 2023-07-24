package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.add_bank.AddBankRequest
import com.example.caregiverphase2.model.pojo.add_bank.AddBankResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddBankRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addBank(
        email: String,
        token: String
    ): Flow<AddBankResponse?> = flow{
        emit(apiInterface.addBank(AddBankRequest(email),token))
    }.flowOn(Dispatchers.IO)
}