package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.signup.SignUpResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: SignUpRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<SignUpResponse?>?>()
    val response: MutableLiveData<Outcome<SignUpResponse?>?> = _response

    fun signup(
        name: String,
        email: String,
        password: String,
        con_password: String
    ) = viewModelScope.launch {
        repository.signup(
            name, email, password, con_password
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