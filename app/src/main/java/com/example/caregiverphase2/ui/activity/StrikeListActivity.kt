package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.databinding.ActivityStrikeListBinding

class StrikeListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStrikeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStrikeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}