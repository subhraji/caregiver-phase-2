package com.example.caregiverphase2.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityFullScreenNotifyBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.AcceptJobViewModel
import isConnectedToInternet
import loadingDialog

class FullScreenNotifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenNotifyBinding
    var cTimer: CountDownTimer? = null
    private lateinit var job_title: String
    private var job_id: String? = null

    private val mAcceptJobViewModel: AcceptJobViewModel by viewModels()
    private lateinit var accessToken: String

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flags: Int = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.addFlags(flags)

        binding= ActivityFullScreenNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

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
            binding.rewardsTv.text = "$rewards"
        }

        startTimer()

        binding.declineBtn.setOnClickListener {
            finish()
        }

        binding.acceptBtn.setOnClickListener {
            acceptPopUp()
            cancelTimer()
        }

        //observer
        acceptJobObserver()
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

    private fun acceptJobObserver(){
        mAcceptJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mAcceptJobViewModel.navigationComplete()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun acceptPopUp(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Accept")
        builder.setMessage("Do you want to accept this job ?")
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(isConnectedToInternet()){
                job_id?.let {
                    mAcceptJobViewModel.acceptJob(it,accessToken)
                    loader.show()
                }
            }else{
                Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }
}