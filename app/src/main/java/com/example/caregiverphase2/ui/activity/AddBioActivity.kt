package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAddBioBinding
import com.example.caregiverphase2.databinding.ActivityAskLocationBinding

class AddBioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearBtn.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            finish()
        }
    }
}