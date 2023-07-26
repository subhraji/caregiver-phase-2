package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.connect_account_status.ConnectAccountStatusResponse
import com.example.caregiverphase2.model.repository.ConnectAccountRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectAccountStatusViewModel @Inject constructor(private val mAddBankRepository: ConnectAccountRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ConnectAccountStatusResponse?>?>()
    val response: MutableLiveData<Outcome<ConnectAccountStatusResponse?>?> = _response

    fun connectAccountAccount(
        token: String
    ) = viewModelScope.launch {
        mAddBankRepository.connectAccountStatus(
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