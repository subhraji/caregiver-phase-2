package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAddBioBinding
import com.example.caregiverphase2.databinding.ActivityEarningsBinding

class EarningsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEarningsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn1.setOnClickListener {
            finish()
        }

        binding.addBankBtn.setOnClickListener {
            val intent = Intent(this, AddBankAccountActivity::class.java)
            startActivity(intent)
        }

    }
}