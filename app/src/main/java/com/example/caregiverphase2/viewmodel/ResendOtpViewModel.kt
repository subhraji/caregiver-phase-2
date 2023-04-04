package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.resend_otp.ResendOtpResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.ResendOtpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResendOtpViewModel @Inject constructor(private val repository: ResendOtpRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ResendOtpResponse?>?>()
    val response: MutableLiveData<Outcome<ResendOtpResponse?>?> = _response

    fun resendOtp(
        email: String
    ) = viewModelScope.launch {
        repository.resendOtp(
            email
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