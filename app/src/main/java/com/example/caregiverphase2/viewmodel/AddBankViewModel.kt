package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.add_bank.AddBankResponse
import com.example.caregiverphase2.model.repository.AddBankRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBankViewModel @Inject constructor(private val mAddBankRepository: AddBankRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AddBankResponse?>?>()
    val response: MutableLiveData<Outcome<AddBankResponse?>?> = _response

    fun addBank(
        email: String,
        token: String
    ) = viewModelScope.launch {
        mAddBankRepository.addBank(
            email, token
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