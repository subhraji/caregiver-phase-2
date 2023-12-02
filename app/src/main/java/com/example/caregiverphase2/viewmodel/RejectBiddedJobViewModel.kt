package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.reject_bidded_job.RejectBiddedJobResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.RejectBiddedJobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RejectBiddedJobViewModel @Inject constructor(private val repository: RejectBiddedJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<RejectBiddedJobResponse?>?>()
    val response: MutableLiveData<Outcome<RejectBiddedJobResponse?>?> = _response

    fun rejectBiddedJob(
        job_id: String,
        token: String
    ) = viewModelScope.launch {
        repository.rejectBiddedJob(
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