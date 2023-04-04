package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.edit_basic_info.EditBasicInfoResponse
import com.example.caregiverphase2.model.repository.EditBasicInfoRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBasicInfoViewModel @Inject constructor(private val repository: EditBasicInfoRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<EditBasicInfoResponse?>?>()
    val response: MutableLiveData<Outcome<EditBasicInfoResponse?>?> = _response

    fun editBasicInfo(
        phone: String,
        experience: String,
        token: String
    ) = viewModelScope.launch {
        repository.editBasicInfo(
            phone, experience, token
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