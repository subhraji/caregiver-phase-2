package com.example.caregiverphase2.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.SignUpViewModel
import com.example.caregiverphase2.viewmodel.SubmitBidViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class JobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobDetailsBinding
    private var start_time: String? = ""
    private lateinit var job_id: String
    var cTimer: CountDownTimer? = null

    private lateinit var accessToken: String

    private val mSubmitBidViewModel: SubmitBidViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            start_time = intent?.getStringExtra("start_time")!!
            job_id = intent?.getStringExtra("id")!!
        }

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        loader = this.loadingDialog()

        //observer
        submitBidObserver()

        startTimer()

        clickJobOverview()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.jobOverviewCard.setOnClickListener {
            clickJobOverview()
        }

        binding.checklistCard.setOnClickListener {
            clickCheckList()
        }

        binding.bidNowTv.setOnClickListener {
            //showCompleteDialog()

            if(isConnectedToInternet()){
                mSubmitBidViewModel.submitBid(job_id,accessToken)
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun clickJobOverview(){
        binding.jobOverviewTv.setBackgroundResource(R.color.theme_blue)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.checkListTv.setBackgroundResource(R.color.white)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay1.visible()
        binding.relativeLay2.gone()

    }

    private fun clickCheckList(){
        binding.checkListTv.setBackgroundResource(R.color.theme_blue)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.jobOverviewTv.setBackgroundResource(R.color.white)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay2.visible()
        binding.relativeLay1.gone()
    }

    private fun getCurrentDateTime():String{
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentDateTime = sdf.format(Date())
        return currentDateTime
    }

    fun startTimer() {
        var secs = 60
        cTimer = object : CountDownTimer(
            ((secs + 1) * 1000).toLong(), 1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                getConditionHour(getCurrentDateTime(),"14-12-2022 "+start_time)
            }
            override fun onFinish() {
                cancelTimer()
            }
        }
        (cTimer as CountDownTimer).start()
    }

    fun cancelTimer() {
        if (cTimer != null) cTimer!!.cancel()
    }

    private fun getConditionHour(currentDateTime: String, startDateTime: String) {

        val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        try {
            val d1: Date = sdf.parse(currentDateTime)
            val d2: Date = sdf.parse(startDateTime)

            val difference_In_Time = d2.time - d1.time

            val difference_In_Seconds = (difference_In_Time / 1000)% 60

            val difference_In_Minutes = (difference_In_Time / (1000 * 60))% 60

            val difference_In_Hours = (difference_In_Time / (1000 * 60 * 60))% 24

            val difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24 * 365))

            var difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24))% 365

            Log.d("dateTime","condition => "+
                    difference_In_Years.toString()+
                    " years, "
                    + difference_In_Days
                    + " days, "
                    + difference_In_Hours
                    + " hours, "
                    + difference_In_Minutes
                    + " minutes, "
                    + difference_In_Seconds
                    + " seconds"
            )

            binding.bidTimeTv.text = difference_In_Hours.toString()+":"+difference_In_Minutes.toString()+":"+difference_In_Seconds.toString()

        }

        // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }


    private fun submitBidObserver(){
        mSubmitBidViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        mSubmitBidViewModel.navigationComplete()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }



}