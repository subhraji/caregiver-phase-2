package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.accept_job.AcceptJobResponse
import com.example.caregiverphase2.model.repository.AcceptJobRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcceptJobViewModel @Inject constructor(private val repository: AcceptJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AcceptJobResponse?>?>()
    val response: MutableLiveData<Outcome<AcceptJobResponse?>?> = _response

    fun acceptJob(
        job_id: String,
        token: String
    ) = viewModelScope.launch {
        repository.acceptJob(
            job_id, token
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