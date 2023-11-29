package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.caregiverphase2.ui.fragment.AgencyFragment
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetBiddedJobsViewModel
import convertDate
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

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

        binding.viewProfileHtv.setOnClickListener {
            /*val intent = Intent(this,AgencyProfileActivity::class.java)
            intent.putExtra("id",job_id.toString())
            startActivity(intent)*/

            val addPhotoBottomDialogFragment: AgencyFragment =
                AgencyFragment.newInstance()
            val bundle = Bundle()
            bundle.putString("id", job_id.toString())
            addPhotoBottomDialogFragment.arguments = bundle
            addPhotoBottomDialogFragment.show(
                supportFragmentManager,
                "agency_profile_fragment"
            )
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
                        binding.dateTv.text = convertDate(outcome.data!!.data[0].startDate.toString())+" to "+convertDate(outcome.data!!.data[0].endDate.toString())
                        binding.timeTv.text = outcome.data!!.data[0].startTime.toString()+" - "+outcome.data!!.data[0].endTime.toString()
                        binding.priceTv.text = "$"+outcome.data!!.data[0].amount.toString()
                        binding.agencyNameTv.text = outcome.data!!.data[0].companyName.toString()
                        binding.jobDescTv.text = outcome.data!!.data[0].description.toString()

                        setTimer(
                            getDurationHour(
                                getCurrentDate(),
                                parseDateToddMMyyyy("${outcome.data!!.data[0].startDate} ${outcome.data!!.data[0].startTime}")!!
                            )
                        )

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
                        outcome.data!!.data[0].expertise?.let {
                            if(outcome.data!!.data[0].expertise.isNotEmpty()){
                                binding.jobExpRecycler.visible()
                                binding.jobExpHtv.visible()
                                jobExpFillRecycler(outcome.data!!.data[0].expertise.toMutableList())
                            }
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

    private fun getDurationHour(startDateTime: String, endDateTime: String): Long {

        val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        var durationTotalMin = 0

        try {
            val d1: Date = sdf.parse(startDateTime)
            val d2: Date = sdf.parse(endDateTime)

            val difference_In_Time = d2.time - d1.time

            val difference_In_Seconds = (difference_In_Time / 1000)% 60

            val difference_In_Minutes = (difference_In_Time / (1000 * 60))% 60

            val difference_In_Hours = (difference_In_Time / (1000 * 60 * 60))% 24

            val difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24 * 365))

            var difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24))% 365

            val durationDay = difference_In_Days.toInt()
            val durationHour = difference_In_Hours.toInt()

            durationTotalMin = (durationHour*60)+difference_In_Minutes.toInt()
        }

        // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }

        return durationTotalMin.toLong()*60*1000
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return sdf.format(Date())
    }

    fun parseDateToddMMyyyy(time: String): String? {
        val inputPattern = "yyyy-MM-dd h:mm a"
        val outputPattern = "dd-MM-yyyy HH:mm:ss"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    private fun setTimer(duration: Long){
        object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var millisUntilFinished = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val elapsedHours = millisUntilFinished / hoursInMilli
                millisUntilFinished = millisUntilFinished % hoursInMilli
                val elapsedMinutes = millisUntilFinished / minutesInMilli
                millisUntilFinished = millisUntilFinished % minutesInMilli
                val elapsedSeconds = millisUntilFinished / secondsInMilli
                val yy = String.format("%02d:%02d:%2d", elapsedHours, elapsedMinutes, elapsedSeconds)
                binding.bidTimeTv.setText(yy)
            }

            override fun onFinish() {
                binding.bidTimeTv.setText("00:00:00")
            }
        }.start()
    }
}