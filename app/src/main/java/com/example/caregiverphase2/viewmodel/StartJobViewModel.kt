package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.start_job.StartJobResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.StartJobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartJobViewModel @Inject constructor(private val repository: StartJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<StartJobResponse?>?>()
    val response: MutableLiveData<Outcome<StartJobResponse?>?> = _response

    fun starJob(
        job_id: Int,
        token: String,
    ) = viewModelScope.launch {
        repository.startJob(
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