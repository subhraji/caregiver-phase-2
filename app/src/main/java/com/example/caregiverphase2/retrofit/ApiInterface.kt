package com.example.caregiverphase2.retrofit

import com.example.caregiverphase2.model.pojo.login.LoginRequest
import com.example.caregiverphase2.model.pojo.login.LoginResponse
import com.example.caregiverphase2.model.pojo.logout.LogoutResponse
import com.example.caregiverphase2.model.pojo.signup.SignUpRequest
import com.example.caregiverphase2.model.pojo.signup.SignUpResponse
import com.example.caregiverphase2.model.pojo.todo.GetTodosResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @GET("todos")
    suspend fun getTodos(): GetTodosResponse?

    @POST("signup")
    suspend fun signup(@Body body: SignUpRequest?): SignUpResponse?

    @POST("login")
    suspend fun login(@Body body: LoginRequest?): LoginResponse?

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): LogoutResponse?
}