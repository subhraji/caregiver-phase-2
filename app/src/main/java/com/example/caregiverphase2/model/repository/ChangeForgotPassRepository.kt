package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.add_review.AddReviewRequest
import com.example.caregiverphase2.model.pojo.add_review.AddReviewResponse
import com.example.caregiverphase2.model.pojo.forgot_pass_change_pass.ChangeForgotPassRequest
import com.example.caregiverphase2.model.pojo.forgot_pass_change_pass.ChangeForgotPassResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChangeForgotPassRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun changeForgotPass(
        email: String,
        password: String,
        confirm_password: String,
        fcm_token: String
    ): Flow<ChangeForgotPassResponse?> = flow{
        emit(apiInterface.changeForgotPass(
            ChangeForgotPassRequest(
                email, password, confirm_password, fcm_token
            )
        ))
    }.flowOn(Dispatchers.IO)
}