package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordResponse
import com.example.caregiverphase2.model.repository.ChangePasswordRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val repository: ChangePasswordRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ChangePasswordResponse?>?>()
    val response: MutableLiveData<Outcome<ChangePasswordResponse?>?> = _response

    fun changePassword(
        current_Password: String,
        password: String,
        con_password: String,
        token: String
    ) = viewModelScope.launch {
        repository.changePassword(
            current_Password, password, con_password, token
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