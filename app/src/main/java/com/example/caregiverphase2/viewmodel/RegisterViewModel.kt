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
        full_address: String,
        short_address: String,
        street: String,
        city: String,
        state: String,
        zipcode: String,
        appartment_or_unit: String? = null,
        floor_no: String? = null,
        country: String,
        token: String
    ) = viewModelScope.launch {
        repository.register(
            photo, phone, dob, gender, ssn, full_address, short_address, street, city, state, zipcode, appartment_or_unit, floor_no, country, token
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
