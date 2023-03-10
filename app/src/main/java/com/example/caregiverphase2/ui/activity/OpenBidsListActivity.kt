package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityOpenBidsListBinding
import com.example.caregiverphase2.databinding.ActivityOpenJobListBinding

class OpenBidsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenBidsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOpenBidsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}