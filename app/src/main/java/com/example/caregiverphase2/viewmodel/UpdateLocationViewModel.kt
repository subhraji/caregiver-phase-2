package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.update_location.UpdateLocationResponse
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.model.repository.UpdateLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLocationViewModel @Inject constructor(private val repository: UpdateLocationRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<UpdateLocationResponse?>?>()
    val response: MutableLiveData<Outcome<UpdateLocationResponse?>?> = _response

    fun updateLocation(
        lat: String,
        long: String,
    ) = viewModelScope.launch {
        repository.updateLocation(
            lat, long
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