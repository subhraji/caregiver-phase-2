package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.connect_refresh_url.ConnectRefreshUrlResponse
import com.example.caregiverphase2.model.repository.ConnectRefreshUrlRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectRefreshUrlViewModel @Inject constructor(private val mAddBankRepository: ConnectRefreshUrlRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ConnectRefreshUrlResponse?>?>()
    val response: MutableLiveData<Outcome<ConnectRefreshUrlResponse?>?> = _response

    fun connectRefreshUrl(
        token: String
    ) = viewModelScope.launch {
        mAddBankRepository.connectRefreshUrl(
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