package com.example.caregiverphase2.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.BulletPointAdapter
import com.example.caregiverphase2.databinding.ActivityJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetOpenBidDetailsViewModel
import com.example.caregiverphase2.viewmodel.GetOpenJobsViewModel
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
    private lateinit var job_type: String
    var cTimer: CountDownTimer? = null

    private lateinit var accessToken: String

    private val mGetOpenJobsViewModel: GetOpenJobsViewModel by viewModels()
    private val mSubmitBidViewModel: SubmitBidViewModel by viewModels()
    private val mGetOpenBidDetailsViewModel: GetOpenBidDetailsViewModel by viewModels()

    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            start_time = intent?.getStringExtra("start_time")!!
            job_id = intent?.getStringExtra("id")!!
            job_type = intent?.getStringExtra("job_type")!!
        }

        //get token
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

        //startTimer()
        //observer
        submitBidObserver()
        getOpenJobsDetailsObserver()
        getOpenBidsDetailsObserver()

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

            showBidPopUp()
        }


        if(job_type == "open_bid"){
            if(isConnectedToInternet()){
                binding.mainLay.gone()
                binding.detailsShimmerView.visible()
                binding.detailsShimmerView.startShimmer()
                mGetOpenBidDetailsViewModel.getOpenBids(accessToken, job_id.toInt())
            }else{
                Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
            }
        }else if(job_type == "open_job"){
            if(isConnectedToInternet()){
                binding.mainLay.gone()
                binding.detailsShimmerView.visible()
                binding.detailsShimmerView.startShimmer()
                mGetOpenJobsViewModel.getOPenJobs(token = accessToken, id = job_id.toInt())
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
                getConditionHour(getCurrentDateTime(),"07-02-2022 "+start_time)
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
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
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

    private fun showBidPopUp(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bid now")
        builder.setMessage("Do you want to bid this job ?")
        builder.setIcon(R.drawable.ic_baseline_logout_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            showEligibilityDialog()
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    private fun showEligibilityDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.bid_check_layout)

        Handler(Looper.getMainLooper()).postDelayed({
            if(isConnectedToInternet()){
                mSubmitBidViewModel.submitBid(job_id,accessToken)
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }, 4000)

        dialog.show()
    }

    private fun getOpenJobsDetailsObserver(){
        mGetOpenJobsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.mainLay.visible()
                    binding.detailsShimmerView.gone()
                    binding.detailsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        //Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
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
                        mGetOpenJobsViewModel.navigationComplete()
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

    private fun getOpenBidsDetailsObserver(){
        mGetOpenBidDetailsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.mainLay.visible()
                    binding.detailsShimmerView.gone()
                    binding.detailsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        //Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        var gen = ""
                        for(i in outcome.data!!.data.careItems){
                            if(gen.isEmpty()){
                                gen = i.gender+": "+i.age
                            }else{
                                gen = gen+", "+i.gender+": "+i.age
                            }
                        }
                        binding.ageTv.text = gen
                        binding.titleTv.text = outcome.data!!.data.jobTitle
                        binding.careTypeTv.text = outcome.data!!.data.careType
                        binding.locTv.text = outcome.data!!.data.shortAddress
                        binding.dateTv.text = outcome.data!!.data.date.toString()
                        binding.timeTv.text = outcome.data!!.data.startTime.toString()+" - "+outcome.data!!.data.endTime.toString()
                        binding.priceTv.text = "$"+outcome.data!!.data.amount.toString()
                        binding.agencyNameTv.text = outcome.data!!.data.companyName.toString()
                        binding.jobDescTv.text = outcome.data!!.data.description.toString()
                        Glide.with(this)
                            .load(Constants.PUBLIC_URL+outcome.data!!.data.companyPhoto) // image url
                            .placeholder(R.color.dash_yellow) // any placeholder to load at start
                            .centerCrop()
                            .into(binding.agencyImgView)
                        mGetOpenJobsViewModel.navigationComplete()

                        if(outcome.data!!.data.medicalHistory.isNotEmpty()){
                            binding.medicalRecycler.visible()
                            binding.medicalHisHtv.visible()
                            medicalHistoryFillRecycler(outcome.data!!.data.medicalHistory.toMutableList())
                        }
                        if(outcome.data!!.data.experties.isNotEmpty()){
                            binding.jobExpRecycler.visible()
                            binding.jobExpHtv.visible()
                            jobExpFillRecycler(outcome.data!!.data.experties.toMutableList())
                        }
                        if(outcome.data!!.data.otherRequirements.isNotEmpty()){
                            binding.otherReqRecycler.visible()
                            binding.otherReqHtv.visible()
                            otherFillRecycler(outcome.data!!.data.otherRequirements.toMutableList())
                        }
                        if(outcome.data!!.data.checkList.isNotEmpty()){
                            binding.checkListRecycler.visible()
                            binding.noCheckListTv.gone()
                            checkListFillRecycler(outcome.data!!.data.checkList.toMutableList())
                        }else{
                            binding.noCheckListTv.visible()
                        }
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
            adapter = BulletPointAdapter(list,this@JobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@JobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@JobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@JobDetailsActivity)
        }
    }
}