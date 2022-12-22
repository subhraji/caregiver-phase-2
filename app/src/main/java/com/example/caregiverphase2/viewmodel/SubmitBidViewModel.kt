package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.SubmitBidRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubmitBidViewModel @Inject constructor(private val repository: SubmitBidRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<SubmitBidResponse?>?>()
    val response: MutableLiveData<Outcome<SubmitBidResponse?>?> = _response

    fun submitBid(
        job_id: String,
        token: String
    ) = viewModelScope.launch {
        repository.submitBid(
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