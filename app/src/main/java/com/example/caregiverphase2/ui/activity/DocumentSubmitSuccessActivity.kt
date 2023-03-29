package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAskLocationBinding
import com.example.caregiverphase2.databinding.ActivityDocumentSubmitSuccessBinding

class DocumentSubmitSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentSubmitSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDocumentSubmitSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.okBtn.setOnClickListener {
            finish()
        }
    }
}