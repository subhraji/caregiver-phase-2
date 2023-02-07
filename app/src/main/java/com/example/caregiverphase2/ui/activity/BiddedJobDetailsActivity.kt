package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.BulletPointAdapter
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.databinding.ActivityBiddedJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
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

        binding.medicalRecycler.gone()
        binding.medicalHisHtv.gone()
        binding.jobExpRecycler.gone()
        binding.jobExpHtv.gone()
        binding.otherReqRecycler.gone()
        binding.otherReqHtv.gone()
        binding.noCheckListTv.gone()
        binding.checkListRecycler.gone()

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

        binding.mainLay.gone()
        binding.detailsShimmerView.visible()
        binding.detailsShimmerView.startShimmer()
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
                    binding.mainLay.visible()
                    binding.detailsShimmerView.gone()
                    binding.detailsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        var gen = ""
                        for(i in outcome.data!!.data[0].careItems){
                            if(gen.isEmpty()){
                                gen = i.gender+": "+i.age
                            }else{
                                gen = gen+", "+i.gender+": "+i.age
                            }
                        }
                        binding.ageTv.text = gen
                        binding.titleTv.text = outcome.data!!.data[0].jobTitle
                        binding.careTypeTv.text = outcome.data!!.data[0].careType
                        binding.locTv.text = outcome.data!!.data[0].shortAddress
                        binding.dateTv.text = outcome.data!!.data[0].date.toString()
                        binding.timeTv.text = outcome.data!!.data[0].startTime.toString()+" - "+outcome.data!!.data[0].endTime.toString()
                        binding.priceTv.text = "$"+outcome.data!!.data[0].amount.toString()
                        binding.agencyNameTv.text = outcome.data!!.data[0].companyName.toString()
                        binding.jobDescTv.text = outcome.data!!.data[0].description.toString()
                        Glide.with(this)
                            .load(Constants.PUBLIC_URL+outcome.data!!.data[0].companyPhoto) // image url
                            .placeholder(R.color.dash_yellow) // any placeholder to load at start
                            .centerCrop()
                            .into(binding.agencyImgView)

                        if(outcome.data!!.data[0].medicalHistory.isNotEmpty()){
                            binding.medicalRecycler.visible()
                            binding.medicalHisHtv.visible()
                            medicalHistoryFillRecycler(outcome.data!!.data[0].medicalHistory.toMutableList())
                        }
                        if(outcome.data!!.data[0].experties.isNotEmpty()){
                            binding.jobExpRecycler.visible()
                            binding.jobExpHtv.visible()
                            jobExpFillRecycler(outcome.data!!.data[0].experties.toMutableList())
                        }
                        if(outcome.data!!.data[0].otherRequirements.isNotEmpty()){
                            binding.otherReqRecycler.visible()
                            binding.otherReqHtv.visible()
                            otherFillRecycler(outcome.data!!.data[0].otherRequirements.toMutableList())
                        }
                        if(outcome.data!!.data[0].checkList.isNotEmpty()){
                            binding.checkListRecycler.visible()
                            binding.noCheckListTv.gone()
                            checkListFillRecycler(outcome.data!!.data[0].checkList.toMutableList())
                        }else{
                            binding.noCheckListTv.visible()
                        }
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

    private fun medicalHistoryFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.medicalRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@BiddedJobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@BiddedJobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@BiddedJobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@BiddedJobDetailsActivity)
        }
    }

}