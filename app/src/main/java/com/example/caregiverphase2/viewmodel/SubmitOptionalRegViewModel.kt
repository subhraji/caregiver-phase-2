package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.register_optional.SubmitOptionalRegResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.SubmitOptionalRegRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubmitOptionalRegViewModel @Inject constructor(private val repository: SubmitOptionalRegRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<SubmitOptionalRegResponse?>?>()
    val response: MutableLiveData<Outcome<SubmitOptionalRegResponse?>?> = _response

    fun submitOptionalReg(
        job_type: String? = null,
        experience: String? = null,
        token: String
    ) = viewModelScope.launch {
        repository.submitOptionalReg(
            job_type, experience, token
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