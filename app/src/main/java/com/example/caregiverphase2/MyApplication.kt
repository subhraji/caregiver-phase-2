package com.example.caregiverphase2

import android.app.Application
import com.example.caregiverphase2.utils.Constants.STRIPE_PUBLISH_KEY
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object{
        lateinit var application: Application
        fun getInstance(): Application{
            return application
        }
    }
}