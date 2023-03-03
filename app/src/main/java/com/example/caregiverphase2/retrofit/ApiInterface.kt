package com.example.caregiverphase2.retrofit

import com.example.caregiverphase2.model.pojo.accept_job.AcceptJobRequest
import com.example.caregiverphase2.model.pojo.accept_job.AcceptJobResponse
import com.example.caregiverphase2.model.pojo.add_bio.AddBioRequest
import com.example.caregiverphase2.model.pojo.add_bio.AddBioResponse
import com.example.caregiverphase2.model.pojo.add_certificate.AddCertificateResponse
import com.example.caregiverphase2.model.pojo.add_education.AddEducationRequest
import com.example.caregiverphase2.model.pojo.add_education.AddEducationResponse
import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordRequest
import com.example.caregiverphase2.model.pojo.change_password.ChangePasswordResponse
import com.example.caregiverphase2.model.pojo.change_profile_pic.ChangeProfilePicResponse
import com.example.caregiverphase2.model.pojo.complete_job_response.CompleteJobRequest
import com.example.caregiverphase2.model.pojo.complete_job_response.CompleteJobResponse
import com.example.caregiverphase2.model.pojo.delete_document.DeleteDocumentRequest
import com.example.caregiverphase2.model.pojo.delete_document.DeleteDocumentResponse
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationRequest
import com.example.caregiverphase2.model.pojo.email_verification.SignUpEmailVerificationResponse
import com.example.caregiverphase2.model.pojo.get_bidded_jobs.GetBiddedJobsResponse
import com.example.caregiverphase2.model.pojo.get_complete_job.GetCompleteJobsResponse
import com.example.caregiverphase2.model.pojo.get_documents.GetDocumentsResponse
import com.example.caregiverphase2.model.pojo.get_jobs.GetJobsResponse
import com.example.caregiverphase2.model.pojo.get_ongoing_job.GetOngoingJobResponse
import com.example.caregiverphase2.model.pojo.get_open_bid_details.GetOpenBidDetailsResponse
import com.example.caregiverphase2.model.pojo.get_open_jobs.GetOpenJobsResponse
import com.example.caregiverphase2.model.pojo.get_profile.GetProfileResponse
import com.example.caregiverphase2.model.pojo.get_profile_status.GetProfileStatusResponse
import com.example.caregiverphase2.model.pojo.get_reg_details.GetRegistrationDetailsResponse
import com.example.caregiverphase2.model.pojo.login.LoginRequest
import com.example.caregiverphase2.model.pojo.login.LoginResponse
import com.example.caregiverphase2.model.pojo.logout.LogoutResponse
import com.example.caregiverphase2.model.pojo.register.RegisterResponse
import com.example.caregiverphase2.model.pojo.register_optional.SubmitOptionalRegRequest
import com.example.caregiverphase2.model.pojo.register_optional.SubmitOptionalRegResponse
import com.example.caregiverphase2.model.pojo.signup.SignUpRequest
import com.example.caregiverphase2.model.pojo.signup.SignUpResponse
import com.example.caregiverphase2.model.pojo.start_job.JobStartRequest
import com.example.caregiverphase2.model.pojo.start_job.StartJobResponse
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidRequest
import com.example.caregiverphase2.model.pojo.submit_bid.SubmitBidResponse
import com.example.caregiverphase2.model.pojo.todo.GetTodosResponse
import com.example.caregiverphase2.model.pojo.upcomming_job.GetUpcommingJobsResponse
import com.example.caregiverphase2.model.pojo.update_doc_status.UpdateDocStatusResponse
import com.example.caregiverphase2.model.pojo.update_location.UpdateLocationRequest
import com.example.caregiverphase2.model.pojo.update_location.UpdateLocationResponse
import com.example.caregiverphase2.model.pojo.upload_documents.UploadDocumentsResponse
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
    @POST("profile/registration/basic-information")
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

    @GET("job/quick-call/get")
    suspend fun getQuickCall(
        @Header("Authorization") token: String,
        @Query("id") id: Int? = null,
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

    @POST("profile/registration/optional-information")
    suspend fun submitOptionalReg(
        @Body body: SubmitOptionalRegRequest?,
        @Header("Authorization") token: String,
    ): SubmitOptionalRegResponse?

    @GET("status/profile-completion-status")
    suspend fun getProfileStatus(
        @Header("Authorization") token: String,
    ): GetProfileStatusResponse?

    @GET("profile/get-details")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): GetProfileResponse?

    @POST("profile/bio/add")
    suspend fun addBio(
        @Body body: AddBioRequest?,
        @Header("Authorization") token: String,
    ): AddBioResponse?

    @POST("profile/education/add")
    suspend fun addEducation(
        @Body body: AddEducationRequest?,
        @Header("Authorization") token: String,
    ): AddEducationResponse?

    @Multipart
    @POST("profile/certificate/add")
    suspend fun addCertificate(
        @Part document: MultipartBody.Part?,
        @Part("certificate_or_course") certificate_or_course: RequestBody,
        @Part("start_year") start_year: RequestBody,
        @Part("end_year") end_year: RequestBody,
        @Header("Authorization") token: String
    ): AddCertificateResponse?

    @Multipart
    @POST("profile/change-photo")
    suspend fun changeProfilePic(
        @Part photo: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): ChangeProfilePicResponse?

    @POST("job/accept-job/accept")
    suspend fun acceptJob(
        @Body body: AcceptJobRequest?,
        @Header("Authorization") token: String,
    ): AcceptJobResponse?

    @GET("job/upcoming/get")
    suspend fun getUpcommingJobs(
        @Header("Authorization") token: String,
        @Query("job_id") job_id: Int,
    ): GetUpcommingJobsResponse?

    @POST("job/start-job/start")
    suspend fun startJob(
        @Body body: JobStartRequest?,
        @Header("Authorization") token: String,
    ): StartJobResponse?

    @GET("job/ongoing/get")
    suspend fun getOngoingJob(
        @Header("Authorization") token: String,
    ): GetOngoingJobResponse?

    @POST("job/complete-job/complete")
    suspend fun completeJob(
        @Body body: CompleteJobRequest?,
        @Header("Authorization") token: String,
    ): CompleteJobResponse?

    @GET("job/complete-job/get")
    suspend fun getCompleteJobs(
        @Header("Authorization") token: String,
    ): GetCompleteJobsResponse?

    @Multipart
    @POST("document/upload")
    suspend fun uploadDocuments(
        @Part document: MultipartBody.Part?,
        @Part("documentCategory") documentCategory : RequestBody,
        @Part("expiry_date") expiry_date: RequestBody? = null,
        @Header("Authorization") token: String
    ): UploadDocumentsResponse?

    @GET("document/get")
    suspend fun getDocuments(
        @Header("Authorization") token: String,
    ): GetDocumentsResponse?

    @POST("document/delete")
    suspend fun deleteDocument(
        @Body body: DeleteDocumentRequest?,
        @Header("Authorization") token: String,
    ): DeleteDocumentResponse?

    @POST("document/update-status")
    suspend fun updateDocStatus(
        @Header("Authorization") token: String,
    ): UpdateDocStatusResponse?

    @GET("profile/registration/get-registration-details")
    suspend fun getRegDetails(
        @Header("Authorization") token: String,
    ): GetRegistrationDetailsResponse?
}