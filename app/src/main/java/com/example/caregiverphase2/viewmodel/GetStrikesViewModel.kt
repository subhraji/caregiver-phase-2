package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_strikes.GetStrikesResonse
import com.example.caregiverphase2.model.repository.GetStrikesRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetStrikesViewModel @Inject constructor(private val repository: GetStrikesRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetStrikesResonse?>?>()
    val response: MutableLiveData<Outcome<GetStrikesResonse?>?> = _response

    fun getStrikes(
        token: String
    ) = viewModelScope.launch {
        repository.getStrikes(
            token
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