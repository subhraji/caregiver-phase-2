package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.delete_bank.DeleteBankResponse
import com.example.caregiverphase2.model.repository.DeleteBankRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteBankViewModel @Inject constructor(private val mAddBankRepository: DeleteBankRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<DeleteBankResponse?>?>()
    val response: MutableLiveData<Outcome<DeleteBankResponse?>?> = _response

    fun deleteBank(
        token: String
    ) = viewModelScope.launch {
        mAddBankRepository.deleteBank(
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