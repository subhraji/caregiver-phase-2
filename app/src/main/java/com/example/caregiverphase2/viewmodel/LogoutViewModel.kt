package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.logout.LogoutResponse
import com.example.caregiverphase2.model.repository.LogoutRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val repository: LogoutRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<LogoutResponse?>?>()
    val response: MutableLiveData<Outcome<LogoutResponse?>?> = _response

    fun logout(
        token: String,
    ) = viewModelScope.launch {
        repository.logout(
            token
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