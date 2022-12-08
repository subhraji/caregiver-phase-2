package com.example.caregiverphase2.model.repository.todo

import com.example.caregiverphase2.model.pojo.todo.GetTodosResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NewRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun getTodos(): Flow<GetTodosResponse?> = flow{
        emit(apiInterface.getTodos())
    }.flowOn(Dispatchers.IO)
}