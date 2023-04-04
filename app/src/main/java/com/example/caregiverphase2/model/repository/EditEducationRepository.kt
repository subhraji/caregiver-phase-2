package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.edit_education.EditEducationRequest
import com.example.caregiverphase2.model.pojo.edit_education.EditEducationResonse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EditEducationRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun editEducation(
        school_name: String,
        degree_name: String,
        start_year: String,
        end_year: String,
        edu_id: String,
        token: String
    ): Flow<EditEducationResonse?> = flow{
        emit(apiInterface.editEducation(EditEducationRequest(school_name,degree_name,start_year,end_year,edu_id), token))
    }.flowOn(Dispatchers.IO)
}