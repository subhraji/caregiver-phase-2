package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.register.RegisterResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: RegisterRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<RegisterResponse?>?>()
    val response: LiveData<Outcome<RegisterResponse?>?> = _response

    fun register(
        photo: MultipartBody.Part?,
        phone: String,
        dob: String,
        gender: String,
        ssn: String,
        experience: String,
        job_type: String,
        street: String,
        city_or_district: String,
        state: String,
        zip_code: String,
        education: String? = null,
        certificate: String? = null,
        token: String
    ) = viewModelScope.launch {
        repository.register(
            photo, phone, dob, gender, ssn, experience, job_type, street, city_or_district, state, zip_code, education, certificate, token
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