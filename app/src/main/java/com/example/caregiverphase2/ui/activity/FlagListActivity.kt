package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.databinding.ActivityFlagListBinding

class FlagListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlagListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFlagListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}