package com.example.caregiverphase2.retrofit

import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordRequest
import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordResponse
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationRequest
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationResponse
import com.example.caregiverphase2.model.pojo.get_bidded_jobs.GetBiddedJobsResponse
import com.example.caregiverphase2.model.pojo.get_jobs.GetJobsResponse
import com.example.caregiverphase2.model.pojo.get_open_bid_details.GetOpenBidDetailsResponse
import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOpenJobsResponse
import com.example.caregiverphase2.model.pojo.login.LoginRequest
import com.example.caregiverphase2.model.pojo.login.LoginResponse
import com.example.caregiverphase2.model.pojo.logout.LogoutResponse
import com.example.caregiverphase2.model.pojo.register.RegisterResponse
import com.example.caregiverphase2.model.pojo.signup.SignUpRequest
import com.example.caregiverphase2.model.pojo.signup.SignUpResponse
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidRequest
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidResponse
import com.example.caregiverphase2.model.pojo.todo.GetTodosResponse
import com.example.caregiverphase2.model.pojo.update_location.UpdateLocationRequest
import com.example.caregiverphase2.model.pojo.update_location.UpdateLocationResponse
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
        @Query("id") id: Int? = null,
    ): GetJobsResponse?

    @Multipart
    @POST("profile/basic-information")
    suspend fun registration(
        @Part photo: MultipartBody.Part?,
        @Part("phone") phone: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("ssn") ssn: RequestBody,
        @Part("full_address") full_address: RequestBody,
        @Part("short_address") short_address: RequestBody,
        @Header("Authorization") token: String
    ): RegisterResponse?

    @POST("job/bidding/submit-bid")
    suspend fun submitBid(
        @Body body: SubmitBidRequest?,
        @Header("Authorization") token: String): SubmitBidResponse?

    @GET("job/get-bidded-jobs")
    suspend fun getOPenBids(
        @Header("Authorization") token: String,
    ): GetOpenJobsResponse?

    @GET("job/quick-call")
    suspend fun getQuickCall(
        @Header("Authorization") token: String,
    ): GetOpenJobsResponse?

    @POST("profile/change-password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequest?,
        @Header("Authorization") token: String,
    ): ChangePasswordResponse?

    @POST("check-email-exist")
    suspend fun getEmailVerificationOtp(
        @Body body: SignUpEmailVerificationRequest?
    ): SignUpEmailVerificationResponse?

    @POST("user/update-location")
    suspend fun updateLocation(
        @Body body: UpdateLocationRequest?,
        @Header("Authorization") token: String,
    ): UpdateLocationResponse?

    @GET("job/get-single-job-for-bidded")
    suspend fun getOpenBidDetails(
        @Header("Authorization") token: String,
        @Query("job_id") id: Int? = null,
    ): GetOpenBidDetailsResponse?

    @GET("job/get-all-my-bidded-jobs")
    suspend fun getBiddedJobs(
        @Header("Authorization") token: String,
        @Query("id") id: Int? = null,
    ): GetBiddedJobsResponse?
}