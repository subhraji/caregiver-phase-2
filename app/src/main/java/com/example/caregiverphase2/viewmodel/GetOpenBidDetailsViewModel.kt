package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_open_bid_details.GetOpenBidDetailsResponse
import com.example.caregiverphase2.model.repository.GetOpenBidDetailsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetOpenBidDetailsViewModel @Inject constructor(private val repository: GetOpenBidDetailsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetOpenBidDetailsResponse?>?>()
    val response: MutableLiveData<Outcome<GetOpenBidDetailsResponse?>?> = _response

    fun getOpenBids(
        token: String,
        job_id: Int
    ) = viewModelScope.launch {
        repository.getOpenBidDetails(
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