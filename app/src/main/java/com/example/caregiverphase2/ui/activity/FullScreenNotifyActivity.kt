package com.example.caregiverphase2.ui.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.Toast
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityFullScreenNotifyBinding

class FullScreenNotifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenNotifyBinding
    var cTimer: CountDownTimer? = null
    private lateinit var job_title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flags: Int = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.addFlags(flags)

        binding= ActivityFullScreenNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            job_title = extras.getString("title").toString()
            binding.jobTitleTv.text = job_title
        }

        startTimer()

        binding.declineBtn.setOnClickListener {
            finish()
        }

        binding.acceptBtn.setOnClickListener {
            finish()
        }
    }


    fun startTimer() {
        cTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerTv.setText("" + millisUntilFinished / 1000 +" sec");
            }
            override fun onFinish() {
                cancelTimer()
                finish()
            }
        }
        (cTimer as CountDownTimer).start()
    }

    fun cancelTimer() {
        if (cTimer != null) cTimer!!.cancel()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }
}