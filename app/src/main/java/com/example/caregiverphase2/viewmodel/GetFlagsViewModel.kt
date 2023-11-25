package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.get_flags.GetFlagsReposnse
import com.example.caregiverphase2.model.repository.GetFlagsRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetFlagsViewModel @Inject constructor(private val repository: GetFlagsRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<GetFlagsReposnse?>?>()
    val response: MutableLiveData<Outcome<GetFlagsReposnse?>?> = _response
    fun getFlags(
        token: String,
    ) = viewModelScope.launch {
        repository.getFlags(
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