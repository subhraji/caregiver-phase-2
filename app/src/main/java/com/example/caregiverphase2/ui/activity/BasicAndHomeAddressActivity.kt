package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.databinding.ActivityJobDetailsBinding

class BasicAndHomeAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasicAndHomeAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBasicAndHomeAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.nextCardBtn.setOnClickListener {
            val intent = Intent(this, EducationAndCertificateActivity::class.java)
            startActivity(intent)
        }
    }
}