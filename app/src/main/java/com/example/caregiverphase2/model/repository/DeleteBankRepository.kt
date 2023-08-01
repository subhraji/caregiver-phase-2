package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.delete_bank.DeleteBankResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteBankRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun deleteBank(
        token: String
    ): Flow<DeleteBankResponse?> = flow{
        emit(apiInterface.deleteBank(token))
    }.flowOn(Dispatchers.IO)
}