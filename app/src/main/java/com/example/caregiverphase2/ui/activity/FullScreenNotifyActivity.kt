package com.example.caregiverphase2.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
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
            val job_amount = extras.getString("job_amount").toString()
            val job_start_date = extras.getString("job_start_date").toString()
            val job_start_time = extras.getString("job_start_time").toString()
            val job_end_date = extras.getString("job_end_date").toString()
            val job_end_time = extras.getString("job_end_time").toString()
            val care_type = extras.getString("care_type").toString()
            val address = extras.getString("address").toString()
            //val care_items = extras.getString("care_items").toString()
            val rewards = extras.getString("rewards").toString()
            binding.jobTitleTv.text = job_title
            binding.dateTv.text = "$job_start_date to $job_end_date"
            binding.timeTv.text = "$job_start_time to $job_end_time"
            binding.careTypeTv.text = "$care_type"
            binding.priceTv.text = "$job_amount"
            binding.addressTv.text = "$address"
        }

        startTimer()

        binding.declineBtn.setOnClickListener {
            finish()
        }

        binding.acceptBtn.setOnClickListener {
            showAcceptDialog()
            cancelTimer()
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

    private fun showAcceptDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.job_accept_success_dialog_layout)

        val okay = dialog.findViewById<TextView>(R.id.ok_btn)

        /*Handler(Looper.getMainLooper()).postDelayed({

        }, 2500)*/
        okay.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }
}