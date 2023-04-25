package com.example.caregiverphase2.model.repository

import com.example.caregiverphase2.model.pojo.add_review.AddReviewRequest
import com.example.caregiverphase2.model.pojo.add_review.AddReviewResponse
import com.example.caregiverphase2.retrofit.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class AddReviewRepository @Inject constructor(private val apiInterface: ApiInterface)  {
    fun addReview(
        job_id: String,
        rating: String,
        review: String,
        token: String
    ): Flow<AddReviewResponse?> = flow{
        emit(apiInterface.addReview(
            AddReviewRequest(
                job_id, rating, review
            ),token
        ))
    }.flowOn(Dispatchers.IO)
}