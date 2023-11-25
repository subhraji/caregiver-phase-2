package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.delete_certificate.DeleteCertificateResponse
import com.example.caregiverphase2.model.repository.DeleteCertificateRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteCertificateViewModel @Inject constructor(private val repository: DeleteCertificateRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<DeleteCertificateResponse?>?>()
    val response: MutableLiveData<Outcome<DeleteCertificateResponse?>?> = _response

    fun deleteCertificate(
        id: String,
        token: String,
    ) = viewModelScope.launch {
        repository.deleteCertificate(
            id, token
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