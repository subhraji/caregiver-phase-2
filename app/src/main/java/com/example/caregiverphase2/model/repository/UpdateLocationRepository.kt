package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.update_location.UpdateLocationRequest
import com.example.caregiverphase2.model.pojo.update_location.UpdateLocationResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateLocationRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun updateLocation(
        lat: String,
        long: String,
    ): Flow<UpdateLocationResponse?> = flow{
        emit(apiInterface.updateLocation(UpdateLocationRequest(lat, long)))
    }.flowOn(Dispatchers.IO)
}