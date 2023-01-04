package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOPenJobsResponse
import com.example.caregiverphase2.model.repository.GetOPenJobsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetOpenJobsViewModel @Inject constructor(private val repository: GetOPenJobsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetOPenJobsResponse?>?>()
    val response: MutableLiveData<Outcome<GetOPenJobsResponse?>?> = _response

    fun getOPenJobs(
        token: String,
        id: Int? = null
    ) = viewModelScope.launch {
        repository.getOPenJobs(
            token, id
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