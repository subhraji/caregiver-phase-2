package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.edit_certificate.EditCertificateResponse
import com.example.caregiverphase2.model.repository.EditCertificateRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class EditCertificateViewModel @Inject constructor(private val repository: EditCertificateRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<EditCertificateResponse?>?>()
    val response: LiveData<Outcome<EditCertificateResponse?>?> = _response

    fun editCertificate(
        document: MultipartBody.Part? = null,
        certificate_or_course: String? = null,
        start_year: String? = null,
        end_year: String? = null,
        cart_id: String,
        token: String
    ) = viewModelScope.launch {
        repository.editCertificate(
            document, certificate_or_course, start_year, end_year, cart_id, token
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