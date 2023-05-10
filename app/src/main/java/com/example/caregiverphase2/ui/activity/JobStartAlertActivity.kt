package com.example.caregiverphase2.ui.activity

import android.R
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.example.caregiverphase2.databinding.ActivityJobStartAlertBinding
import java.io.IOException


class JobStartAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobStartAlertBinding
    private var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobStartAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.okBtn.setOnClickListener {
            finish()
        }

        mp = MediaPlayer()
        try {
            mp?.setDataSource("https://peaceworc-phase2-live.ekodusproject.tech/Caregiver/Uploads/mp3/siren.mp3")
            mp?.prepare()
            mp?.start()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
         mp = null;
    }
}