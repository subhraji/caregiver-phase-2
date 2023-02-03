package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.add_certificate.AddCertificateResponse
import com.example.caregiverphase2.model.repository.AddCertificateRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AddCertificateViewModel @Inject constructor(private val repository: AddCertificateRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AddCertificateResponse?>?>()
    val response: LiveData<Outcome<AddCertificateResponse?>?> = _response

    fun addCertificate(
        document: MultipartBody.Part?,
        certificate_or_course: String,
        start_year: String,
        end_year: String,
        token: String
    ) = viewModelScope.launch {
        repository.addCertificate(
            document, certificate_or_course, start_year, end_year, token
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