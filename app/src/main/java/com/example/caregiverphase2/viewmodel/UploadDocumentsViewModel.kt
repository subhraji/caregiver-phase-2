package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.upload_documents.UploadDocumentsResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.UploadDocumentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UploadDocumentsViewModel @Inject constructor(private val repository: UploadDocumentsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<UploadDocumentsResponse?>?>()
    val response: LiveData<Outcome<UploadDocumentsResponse?>?> = _response

    fun uploadDocuments(
        document: MultipartBody.Part?,
        documentCategory: String,
        expiry_date: String,
        token: String
    ) = viewModelScope.launch {
        repository.uploadDocuments(
            document, documentCategory, expiry_date, token
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