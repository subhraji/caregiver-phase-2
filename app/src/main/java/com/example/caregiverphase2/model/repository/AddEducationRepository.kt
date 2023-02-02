package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.add_education.AddEducationRequest
import com.example.caregiverphase2.model.pojo.add_education.AddEducationResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddEducationRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addEducation(
        school_name: String,
        degree_name: String,
        start_year: String,
        end_year: String,
        token: String
    ): Flow<AddEducationResponse?> = flow{
        emit(apiInterface.addEducation(AddEducationRequest(school_name,degree_name,start_year,end_year), token))
    }.flowOn(Dispatchers.IO)
}