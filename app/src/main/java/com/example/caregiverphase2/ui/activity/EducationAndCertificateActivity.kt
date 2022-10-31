package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityEducationAndCertificateBinding
import com.example.caregiverphase2.databinding.ActivitySplashBinding

class EducationAndCertificateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEducationAndCertificateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEducationAndCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}