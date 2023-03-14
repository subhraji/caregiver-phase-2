package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityStrikeListBinding
import com.example.caregiverphase2.databinding.ActivityStrikeRemoveBinding

class StrikeRemoveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStrikeRemoveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStrikeRemoveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.submitBtn.setOnClickListener {
            finish()
        }
    }
}