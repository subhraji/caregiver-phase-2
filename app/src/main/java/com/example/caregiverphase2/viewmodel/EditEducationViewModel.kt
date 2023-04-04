package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.edit_education.EditEducationResonse
import com.example.caregiverphase2.model.repository.EditEducationRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEducationViewModel @Inject constructor(private val repository: EditEducationRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<EditEducationResonse?>?>()
    val response: MutableLiveData<Outcome<EditEducationResonse?>?> = _response

    fun editEducation(
        school_name: String,
        degree_name: String,
        start_year: String,
        end_year: String,
        edu_id: String,
        token: String
    ) = viewModelScope.launch {
        repository.editEducation(
            school_name, degree_name, start_year, end_year, edu_id, token
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