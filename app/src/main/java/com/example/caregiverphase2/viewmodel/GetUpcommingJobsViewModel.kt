package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.upcomming_job.GetUpcommingJobsResponse
import com.example.caregiverphase2.model.repository.GetUpcommingJobsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetUpcommingJobsViewModel @Inject constructor(private val repository: GetUpcommingJobsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetUpcommingJobsResponse?>?>()
    val response: MutableLiveData<Outcome<GetUpcommingJobsResponse?>?> = _response

    fun getUpcommingJobs(
        token: String,
        job_id: Int
    ) = viewModelScope.launch {
        repository.getUpcommingJobs(
            token, job_id
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