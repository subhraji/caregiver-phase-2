package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.databinding.ActivityBiddedJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetBiddedJobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class BiddedJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBiddedJobDetailsBinding
    private lateinit var accessToken: String
    private var start_time: String? = ""
    private lateinit var job_id: String
    private lateinit var job_type: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mGetBiddedJobsViewModel: GetBiddedJobsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBiddedJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras != null) {
            start_time = intent?.getStringExtra("start_time")!!
            job_id = intent?.getStringExtra("id")!!
            job_type = intent?.getStringExtra("job_type")!!
        }
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

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

        if(isConnectedToInternet()){
            mGetBiddedJobsViewModel.getBiddedJobs(accessToken,job_id.toInt())
        }else{
            Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
        }

        //observer
        getBiddedJobsObserver()
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

    private fun getBiddedJobsObserver(){
        mGetBiddedJobsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{

                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()

                        mGetBiddedJobsViewModel.navigationComplete()
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