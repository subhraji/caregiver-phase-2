package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.forgot_pass_send_email.ForgotPassSendEmailResponse
import com.example.caregiverphase2.model.repository.ForgotPassSendEmailRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ForgotPassSendEmailViewModel @Inject constructor(private val repository: ForgotPassSendEmailRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ForgotPassSendEmailResponse?>?>()
    val response: MutableLiveData<Outcome<ForgotPassSendEmailResponse?>?> = _response

    fun forgotPassSendEmail(
        email: String,
    ) = viewModelScope.launch {
        repository.forgotPassSendEmail(
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