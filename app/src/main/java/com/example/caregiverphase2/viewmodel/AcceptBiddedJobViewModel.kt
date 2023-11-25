package com.example.caregiverphase2.viewmodel

import android.security.identity.AccessControlProfile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.accept_bidded_job.AcceptBiddedJobResponse
import com.example.caregiverphase2.model.repository.AcceptBiddedJobsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcceptBiddedJobViewModel @Inject constructor(private val repository: AcceptBiddedJobsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AcceptBiddedJobResponse?>?>()
    val response: MutableLiveData<Outcome<AcceptBiddedJobResponse?>?> = _response

    fun acceptBiddedJob(
        job_id: String,
        token: String
    ) = viewModelScope.launch {
        repository.acceptBiddedJob(
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