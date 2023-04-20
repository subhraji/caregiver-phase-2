package com.example.caregiverphase2.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.BulletPointAdapter
import com.example.caregiverphase2.databinding.ActivityMainBinding
import com.example.caregiverphase2.databinding.ActivityQuickCallsDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.*
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
class QuickCallsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuickCallsDetailsBinding

    private lateinit var accessToken: String
    private val mGetQuickCallViewModel: GetQuickCallViewModel by viewModels()
    private val mAcceptJobViewModel: AcceptJobViewModel by viewModels()

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private var start_time: String? = ""
    private lateinit var job_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuickCallsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            start_time = intent?.getStringExtra("start_time")!!
            job_id = intent?.getStringExtra("id")!!
        }

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

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

        binding.acceptTv.setOnClickListener {
            acceptPopUp()
        }

        binding.viewProfileHtv.setOnClickListener {
            val intent = Intent(this,AgencyProfileActivity::class.java)
            intent.putExtra("id",job_id.toString())
            startActivity(intent)
        }

        //observer
        getQuickCallObserver()
        acceptJobObserver()

        if(isConnectedToInternet()){
            binding.mainLay.gone()
            binding.detailsShimmerView.visible()
            binding.detailsShimmerView.startShimmer()
            mGetQuickCallViewModel.getQuickCall(accessToken,job_id.toInt())
        }else{
            Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
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

    private fun getQuickCallObserver(){
        mGetQuickCallViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.mainLay.visible()
                    binding.detailsShimmerView.gone()
                    binding.detailsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        var gen = ""
                        for(i in outcome.data!!.data[0].care_items){
                            if(gen.isEmpty()){
                                gen = i.gender+": "+i.age
                            }else{
                                gen = gen+", "+i.gender+": "+i.age
                            }
                        }
                        binding.ageTv.text = gen
                        binding.titleTv.text = outcome.data!!.data[0].job_title
                        binding.careTypeTv.text = outcome.data!!.data[0].care_type
                        binding.locTv.text = outcome.data!!.data[0].short_address
                        binding.dateTv.text = outcome.data!!.data[0].start_date.toString()+" to "+outcome.data!!.data[0].end_date.toString()
                        binding.timeTv.text = outcome.data!!.data[0].start_time.toString()+" - "+outcome.data!!.data[0].end_time.toString()
                        binding.priceTv.text = "$"+outcome.data!!.data[0].amount.toString()
                        binding.agencyNameTv.text = outcome.data!!.data[0].company_name.toString()
                        binding.jobDescTv.text = outcome.data!!.data[0].description.toString()

                        setTimer(
                            getDurationHour(
                                getCurrentDate(),
                                parseDateToddMMyyyy("${outcome.data!!.data[0].start_date} ${outcome.data!!.data[0].start_time}")!!
                            )
                        )

                        Glide.with(this)
                            .load(Constants.PUBLIC_URL+outcome.data!!.data[0].company_photo) // image url
                            .placeholder(R.color.dash_yellow) // any placeholder to load at start
                            .centerCrop()
                            .into(binding.agencyImgView)

                        if(outcome.data!!.data[0].medical_history.isNotEmpty()){
                            binding.medicalRecycler.visible()
                            binding.medicalHisHtv.visible()
                            medicalHistoryFillRecycler(outcome.data!!.data[0].medical_history.toMutableList())
                        }
                        outcome.data!!.data[0].expertise?.let {
                            if(outcome.data!!.data[0].expertise.isNotEmpty()){
                                binding.jobExpRecycler.visible()
                                binding.jobExpHtv.visible()
                                jobExpFillRecycler(outcome.data!!.data[0].expertise.toMutableList())
                            }
                        }

                        if(outcome.data!!.data[0].other_requirements.isNotEmpty()){
                            binding.otherReqRecycler.visible()
                            binding.otherReqHtv.visible()
                            otherFillRecycler(outcome.data!!.data[0].other_requirements.toMutableList())
                        }
                        if(outcome.data!!.data[0].check_list.isNotEmpty()){
                            binding.checkListRecycler.visible()
                            binding.noCheckListTv.gone()
                            checkListFillRecycler(outcome.data!!.data[0].check_list.toMutableList())
                        }else{
                            binding.noCheckListTv.visible()
                        }
                        mGetQuickCallViewModel.navigationComplete()
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
            adapter = BulletPointAdapter(list,this@QuickCallsDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@QuickCallsDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@QuickCallsDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@QuickCallsDetailsActivity)
        }
    }


    private fun acceptJobObserver(){
        mAcceptJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        binding.acceptLayout.gone()
                        showAcceptDialog()
                        mAcceptJobViewModel.navigationComplete()
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
                mAcceptJobViewModel.acceptJob(job_id,accessToken)
                loader.dismiss()
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