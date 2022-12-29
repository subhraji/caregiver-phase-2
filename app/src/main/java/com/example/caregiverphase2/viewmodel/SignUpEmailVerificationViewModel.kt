package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.SignUpEmailVerifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpEmailVerificationViewModel @Inject constructor(private val repository: SignUpEmailVerifyRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<SignUpEmailVerificationResponse?>?>()
    val response: MutableLiveData<Outcome<SignUpEmailVerificationResponse?>?> = _response

    fun getOtp(
        email: String
    ) = viewModelScope.launch {
        repository.getEmailVerificationOtp(
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