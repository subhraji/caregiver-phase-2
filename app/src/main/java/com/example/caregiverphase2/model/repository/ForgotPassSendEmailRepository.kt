package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.edit_education.EditEducationRequest
import com.example.caregiverphase2.model.pojo.edit_education.EditEducationResonse
import com.example.caregiverphase2.model.pojo.forgot_pass_send_email.ForgotPassSendEmailRequest
import com.example.caregiverphase2.model.pojo.forgot_pass_send_email.ForgotPassSendEmailResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotPassSendEmailRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun forgotPassSendEmail(
        email: String,
    ): Flow<ForgotPassSendEmailResponse?> = flow{
        emit(apiInterface.forgotPassSendEmail(ForgotPassSendEmailRequest(email)))
    }.flowOn(Dispatchers.IO)
}