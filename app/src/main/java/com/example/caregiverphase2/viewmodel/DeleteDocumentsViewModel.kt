package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.delete_document.DeleteDocumentResponse
import com.example.caregiverphase2.model.repository.DeleteDocumentsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteDocumentsViewModel @Inject constructor(private val repository: DeleteDocumentsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<DeleteDocumentResponse?>?>()
    val response: MutableLiveData<Outcome<DeleteDocumentResponse?>?> = _response

    fun deleteDocument(
        documentCategory: String,
        document_id: Int,
        token: String,
    ) = viewModelScope.launch {
        repository.deleteDocument(
            documentCategory, document_id, token
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