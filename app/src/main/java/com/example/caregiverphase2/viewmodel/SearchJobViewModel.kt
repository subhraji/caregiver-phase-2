package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.search_job.GetSearchResultResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.SearchJobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchJobViewModel @Inject constructor(private val repository: SearchJobRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetSearchResultResponse?>?>()
    val response: MutableLiveData<Outcome<GetSearchResultResponse?>?> = _response

    fun searchJob(
        care_type: String?,
        amount_from: String?,
        amount_to: String?,
        token: String
    ) = viewModelScope.launch {
        repository.searchJob(
            care_type, amount_from, amount_to, token
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