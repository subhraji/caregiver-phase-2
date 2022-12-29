package com.example.caregiverphase2.retrofit

import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordRequest
import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordResponse
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationRequest
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationResponse
import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOPenJobsResponse
import com.example.caregiverphase2.model.pojo.login.LoginRequest
import com.example.caregiverphase2.model.pojo.login.LoginResponse
import com.example.caregiverphase2.model.pojo.logout.LogoutResponse
import com.example.caregiverphase2.model.pojo.register.RegisterResponse
import com.example.caregiverphase2.model.pojo.signup.SignUpRequest
import com.example.caregiverphase2.model.pojo.signup.SignUpResponse
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidRequest
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidResponse
import com.example.caregiverphase2.model.pojo.todo.GetTodosResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiInterface {
    @GET("todos")
    suspend fun getTodos(): GetTodosResponse?

    @POST("signup")
    suspend fun signup(@Body body: SignUpRequest?): SignUpResponse?

    @POST("login")
    suspend fun login(@Body body: LoginRequest?): LoginResponse?

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): LogoutResponse?

    @GET("job/get-jobs")
    suspend fun getOpenJobs(
        @Header("Authorization") token: String,
    ): GetOPenJobsResponse?

    @Multipart
    @POST("profile/register")
    suspend fun registration(
        @Part photo: MultipartBody.Part?,
        @Part("phone") phone: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("ssn") ssn: RequestBody,
        @Part("experience") experience: RequestBody,
        @Part("job_type") job_type: RequestBody,
        @Part("street") street: RequestBody,
        @Part("city_or_district") city_or_district: RequestBody,
        @Part("state") state: RequestBody,
        @Part("zip_code") zip_code: RequestBody,
        @Part("education") education: RequestBody? = null,
        @Part("certificate") certificate: RequestBody? = null,
        @Header("Authorization") token: String
    ): RegisterResponse?


    @POST("job/bidding/submit-bid")
    suspend fun submitBid(
        @Body body: SubmitBidRequest?,
        @Header("Authorization") token: String): SubmitBidResponse?

    @GET("job/get-bidded-jobs")
    suspend fun getOPenBids(
        @Header("Authorization") token: String,
    ): GetOPenJobsResponse?

    @GET("job/quick-call")
    suspend fun getQuickCall(
        @Header("Authorization") token: String,
    ): GetOPenJobsResponse?

    @POST("profile/change-password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequest?,
        @Header("Authorization") token: String,
    ): ChangePasswordResponse?

    @POST("check-email-exist")
    suspend fun getEmailVerificationOtp(
        @Body body: SignUpEmailVerificationRequest?
    ): SignUpEmailVerificationResponse?
}