package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.get_bank_details.GetBankDetailsResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetBankDetailsRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getBankDetails(
        token: String,
    ): Flow<GetBankDetailsResponse?> = flow{
        emit(apiInterface.getBankDetails(token))
    }.flowOn(Dispatchers.IO)
}