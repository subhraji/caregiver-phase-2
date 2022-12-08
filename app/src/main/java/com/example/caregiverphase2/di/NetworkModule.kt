package com.example.caregiverphase2.di

import com.example.caregiverphase2.BuildConfig
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.retrofit.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    private val mLoggingInterceptor = HttpLoggingInterceptor()

    private fun getHttpLogClientWithToken(): OkHttpClient {
        if (BuildConfig.DEBUG) {
            mLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(mLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }


    @Singleton
    @Provides
    fun providesRetrofit() : Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpLogClientWithToken())
            .build()
    }

    @Singleton
    @Provides
    fun providesFakerAPI(retrofit: Retrofit) : ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}