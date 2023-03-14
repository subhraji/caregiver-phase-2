package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityFlagListBinding
import com.example.caregiverphase2.databinding.ActivityStrikeSystemBinding

class StrikeSystemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStrikeSystemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStrikeSystemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}