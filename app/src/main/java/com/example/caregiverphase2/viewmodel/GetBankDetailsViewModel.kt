package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_bank_details.GetBankDetailsResponse
import com.example.caregiverphase2.model.repository.GetBankDetailsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetBankDetailsViewModel @Inject constructor(private val repository: GetBankDetailsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetBankDetailsResponse?>?>()
    val response: MutableLiveData<Outcome<GetBankDetailsResponse?>?> = _response

    fun getBankDetails(
        token: String,
    ) = viewModelScope.launch {
        repository.getBankDetails(
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