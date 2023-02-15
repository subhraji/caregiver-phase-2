package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.complete_job_response.CompleteJobResponse
import com.example.caregiverphase2.model.repository.CompleteJobRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteJobViewModel @Inject constructor(private val repository: CompleteJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<CompleteJobResponse?>?>()
    val response: MutableLiveData<Outcome<CompleteJobResponse?>?> = _response

    fun completeJob(
        job_id: Int,
        token: String,
    ) = viewModelScope.launch {
        repository.completeJob(
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