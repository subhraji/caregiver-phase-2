package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.change_profile_pic.ChangeProfilePicResponse
import com.example.caregiverphase2.model.repository.ChangeProfilePicRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ChangeProfilePicViewModel @Inject constructor(private val repository: ChangeProfilePicRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<ChangeProfilePicResponse?>?>()
    val response: LiveData<Outcome<ChangeProfilePicResponse?>?> = _response

    fun changeProfilePic(
        photo: MultipartBody.Part?,
        token: String
    ) = viewModelScope.launch {
        repository.changeProfilePic(
            photo, token
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