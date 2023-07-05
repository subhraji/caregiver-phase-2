package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.upload_chat_image.UploadChatImageResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.UploadChatImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UploadChatImageViewModel @Inject constructor(private val repository: UploadChatImageRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<UploadChatImageResponse?>?>()
    val response: LiveData<Outcome<UploadChatImageResponse?>?> = _response

    fun uploadChatImage(
        image: MultipartBody.Part?,
        sent_by: String,
        token: String
    ) = viewModelScope.launch {
        repository.uploadChatImage(
            image, sent_by, token
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