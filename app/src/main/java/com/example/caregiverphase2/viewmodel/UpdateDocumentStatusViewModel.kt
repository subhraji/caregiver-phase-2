package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.update_doc_status.UpdateDocStatusResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.UpdateDocumentStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateDocumentStatusViewModel @Inject constructor(private val repository: UpdateDocumentStatusRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<UpdateDocStatusResponse?>?>()
    val response: MutableLiveData<Outcome<UpdateDocStatusResponse?>?> = _response

    fun updateDocStatus(
        token: String
    ) = viewModelScope.launch {
        repository.updateDocumentStatus(
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