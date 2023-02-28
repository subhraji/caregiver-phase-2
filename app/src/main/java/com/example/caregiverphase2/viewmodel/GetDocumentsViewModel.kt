package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_documents.GetDocumentsResponse
import com.example.caregiverphase2.model.repository.GetDocumentsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetDocumentsViewModel @Inject constructor(private val repository: GetDocumentsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetDocumentsResponse?>?>()
    val response: MutableLiveData<Outcome<GetDocumentsResponse?>?> = _response

    fun getDocuments(
        token: String,
    ) = viewModelScope.launch {
        repository.getDocuments(
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