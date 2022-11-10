package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAskLocationBinding
import com.example.caregiverphase2.databinding.ActivityAuthBinding

class AskLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAskLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAskLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.useLocBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}