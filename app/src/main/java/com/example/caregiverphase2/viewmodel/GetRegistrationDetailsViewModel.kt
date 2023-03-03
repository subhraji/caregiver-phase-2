package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_reg_details.GetRegistrationDetailsResponse
import com.example.caregiverphase2.model.repository.GetRegistrationDetailsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetRegistrationDetailsViewModel @Inject constructor(private val repository: GetRegistrationDetailsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetRegistrationDetailsResponse?>?>()
    val response: MutableLiveData<Outcome<GetRegistrationDetailsResponse?>?> = _response

    fun getRegDetails(
        token: String
    ) = viewModelScope.launch {
        repository.getRegDetails(
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