package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAddBankAccountBinding
import com.example.caregiverphase2.databinding.ActivityEarningsBinding

class AddBankAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBankAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBankAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearBtn.setOnClickListener {
            finish()
        }
    }
}