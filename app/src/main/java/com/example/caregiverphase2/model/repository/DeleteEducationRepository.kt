package com.example.caregiverphase2.model.repository
import com.example.caregiverphase2.model.pojo.delete_education.DeleteEducationRequest
import com.example.caregiverphase2.model.pojo.delete_education.DeleteEducationResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteEducationRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun deleteEducation(
        id: String,
        token: String,
    ): Flow<DeleteEducationResponse?> = flow{
        emit(apiInterface.deleteEdu(DeleteEducationRequest(id),token))
    }.flowOn(Dispatchers.IO)
}