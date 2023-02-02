package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.add_education.AddEducationResponse
import com.example.caregiverphase2.model.repository.AddEducationRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEducationViewModel @Inject constructor(private val repository: AddEducationRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<AddEducationResponse?>?>()
    val response: MutableLiveData<Outcome<AddEducationResponse?>?> = _response

    fun addEducation(
        school_name: String,
        degree_name: String,
        start_year: String,
        end_year: String,
        token: String
    ) = viewModelScope.launch {
        repository.addEducation(
            school_name, degree_name, start_year, end_year, token
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