package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityMainBinding
import com.example.caregiverphase2.databinding.ActivityQuickCallListBinding

class QuickCallListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuickCallListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuickCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}