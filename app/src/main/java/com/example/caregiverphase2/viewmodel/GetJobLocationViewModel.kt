package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_job_locations.GetJobLocationResponse
import com.example.caregiverphase2.model.repository.GetJobLocationRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetJobLocationViewModel @Inject constructor(private val repository: GetJobLocationRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetJobLocationResponse?>?>()
    val response: MutableLiveData<Outcome<GetJobLocationResponse?>?> = _response

    fun getJobLocation(
        token: String,
        current_lat: String,
        current_long: String
    ) = viewModelScope.launch {
        repository.getJobLocation(
            token,
            current_lat,
            current_long
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