package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityOpenJobListBinding
import com.example.caregiverphase2.databinding.ActivityQuickCallListBinding

class OpenJobListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenJobListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOpenJobListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}