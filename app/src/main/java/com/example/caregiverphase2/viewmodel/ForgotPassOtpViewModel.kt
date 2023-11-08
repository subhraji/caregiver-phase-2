package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.forgot_pass_otp.ForgotPassOtpResponse
import com.example.caregiverphase2.model.repository.ForgotPassOtpRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPassOtpViewModel  @Inject constructor(private val repository: ForgotPassOtpRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ForgotPassOtpResponse?>?>()
    val response: MutableLiveData<Outcome<ForgotPassOtpResponse?>?> = _response
    fun forgotPassSendOtp(
        email: String,
        otp: String
    ) = viewModelScope.launch {
        repository.forgotPassOtp(
            email,
            otp
        ).onStart {
            _response.value = Outcome.loading(true)
        }.catch {
            _response.value = Outcome.Failure(it)
        }.collect {
            _response.value = Outcome.success(it)
        }
    }
    fun navigationComplete(){
        _response.value = null
    }
}