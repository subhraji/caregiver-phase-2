package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityFullScreenNotifyBinding

class FullScreenNotifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenNotifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flags: Int = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.addFlags(flags)

        binding= ActivityFullScreenNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}