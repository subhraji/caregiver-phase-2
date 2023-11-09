package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.forgot_pass_change_pass.ChangeForgotPassResponse
import com.example.caregiverphase2.model.repository.ChangeForgotPassRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeForgotPassViewModel @Inject constructor(private val repository: ChangeForgotPassRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ChangeForgotPassResponse?>?>()
    val response: MutableLiveData<Outcome<ChangeForgotPassResponse?>?> = _response

    fun changeForgotPass(
        email: String,
        password: String,
        confirm_password: String,
        fcm_token: String
    ) = viewModelScope.launch {
        repository.changeForgotPass(
            email, password, confirm_password, fcm_token
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