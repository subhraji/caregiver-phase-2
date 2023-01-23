package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAddBioBinding
import com.example.caregiverphase2.databinding.ActivityAddEducationBinding

class AddEducationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEducationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddEducationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearBtn.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            finish()
        }
    }
}