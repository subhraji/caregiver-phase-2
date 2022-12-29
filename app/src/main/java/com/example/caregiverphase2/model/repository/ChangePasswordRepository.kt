package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordRequest
import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChangePasswordRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun changePassword(
        current_pass: String,
        password: String,
        con_password: String,
        token: String
    ): Flow<ChangePasswordResponse?> = flow{
        emit(apiInterface.changePassword(ChangePasswordRequest(current_pass,password,con_password), token))
    }.flowOn(Dispatchers.IO)
}