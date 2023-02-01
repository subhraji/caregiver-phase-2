package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.add_bio.AddBioResponse
import com.example.caregiverphase2.model.repository.AddBioRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBioViewModel @Inject constructor(private val repository: AddBioRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AddBioResponse?>?>()
    val response: MutableLiveData<Outcome<AddBioResponse?>?> = _response

    fun addBio(
        bio: String,
        token: String
    ) = viewModelScope.launch {
        repository.addBio(
            bio, token
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