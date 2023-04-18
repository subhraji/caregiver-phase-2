package com.example.caregiverphase2.ui.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.BulletPointAdapter
import com.example.caregiverphase2.databinding.ActivityUpcommingJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetUpcommingJobsViewModel
import com.example.caregiverphase2.viewmodel.StartJobViewModel
import com.ncorti.slidetoact.SlideToActView
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class UpcommingJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpcommingJobDetailsBinding

    private val mGetUpcommingJobsViewModel: GetUpcommingJobsViewModel by viewModels()
    private val mStartJobViewModel: StartJobViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    private lateinit var accessToken: String

    private var start_time: String? = ""
    private lateinit var job_id: String

    private var lat: String = ""
    private var long: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpcommingJobDetailsBinding.inflate(layoutInflater)
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
        binding.getDirectionBtn.gone()

        clickJobOverview()

        binding.jobOverviewCard.setOnClickListener {
            clickJobOverview()
        }
        binding.checklistCard.setOnClickListener {
            clickCheckList()
        }
        binding.backBtn.setOnClickListener {
            finish()
        }


        binding.cancelLayout.gone()
        binding.cancelTv.setOnClickListener {
            showCancelDialog()
        }

        //swipe
        binding.slideToStartBtn.onSlideCompleteListener = (object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                binding.slideToStartBtn.resetSlider()
            }
        })

        binding.slideToStartBtn.onSlideToActAnimationEventListener = (object : SlideToActView.OnSlideToActAnimationEventListener{
            override fun onSlideCompleteAnimationEnded(view: SlideToActView) {
                //Toast.makeText(this@UpcommingJobDetailsActivity,"onSlideCompleteAnimationEnded",Toast.LENGTH_SHORT).show()
            }

            override fun onSlideCompleteAnimationStarted(view: SlideToActView, threshold: Float) {
                //Toast.makeText(this@UpcommingJobDetailsActivity,"onSlideCompleteAnimationStarted",Toast.LENGTH_SHORT).show()

            }

            override fun onSlideResetAnimationEnded(view: SlideToActView) {
                //Toast.makeText(this@UpcommingJobDetailsActivity,"onSlideResetAnimationEnded",Toast.LENGTH_SHORT).show()
            }

            override fun onSlideResetAnimationStarted(view: SlideToActView) {
                if(isConnectedToInternet()){
                    mStartJobViewModel.starJob(job_id.toInt(),accessToken)
                    loader.show()
                }else{
                    Toast.makeText(this@UpcommingJobDetailsActivity,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }

        })

        binding.mainLay.gone()
        binding.detailsShimmerView.visible()
        binding.detailsShimmerView.startShimmer()
        if(isConnectedToInternet()){
            mGetUpcommingJobsViewModel.getUpcommingJobs(accessToken,job_id.toInt())
        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
        }

        //observer
        getUpcommingJoobsObserver()
        startJobObserver()

        binding.getDirectionBtn.setOnClickListener {
            if(!lat.isEmpty() && !long.isEmpty()){
                getDirection(lat, long)
            }else{
                Toast.makeText(this,"Could not fetch the location",Toast.LENGTH_SHORT).show()
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

    private fun getUpcommingJoobsObserver(){
        mGetUpcommingJobsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.detailsShimmerView.gone()
                        binding.detailsShimmerView.stopShimmer()
                        if(outcome.data?.data!!.isNotEmpty()){
                            binding.mainLay.visible()

                            var gen = ""
                            for(i in outcome.data!!.data[0].careItems){
                                if(gen.isEmpty()){
                                    gen = i.gender+": "+i.age
                                }else{
                                    gen = gen+", "+i.gender+": "+i.age
                                }
                            }
                            binding.ageTv.text = gen
                            binding.titleTv.text = outcome.data!!.data[0].title
                            binding.careTypeTv.text = outcome.data!!.data[0].careType
                            binding.locTv.text = outcome.data!!.data[0].address.toString()
                            binding.dateTv.text = outcome.data!!.data[0].startDate.toString()+" to "+outcome.data!!.data[0].endDate.toString()
                            binding.timeTv.text = outcome.data!!.data[0].startTime.toString()+" - "+outcome.data!!.data[0].endTime.toString()
                            binding.priceTv.text = "$"+outcome.data!!.data[0].amount.toString()
                            binding.agencyNameTv.text = outcome.data!!.data[0].agencyName.toString()
                            binding.jobDescTv.text = outcome.data!!.data[0].description.toString()
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+outcome.data!!.data[0].agencyPhoto) // image url
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
                            outcome.data!!.data[0].lat?.let {
                                outcome.data!!.data[0].long?.let {
                                    lat = outcome.data!!.data[0].lat
                                    long = outcome.data!!.data[0].long
                                    binding.getDirectionBtn.visible()
                                }
                            }
                        }else{
                            binding.mainLay.gone()
                            binding.detailsShimmerView.visible()
                            binding.detailsShimmerView.startShimmer()
                        }
                        mGetUpcommingJobsViewModel.navigationComplete()
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
            adapter = BulletPointAdapter(list,this@UpcommingJobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@UpcommingJobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@UpcommingJobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@UpcommingJobDetailsActivity)
        }
    }

    private fun startJobObserver(){
        mStartJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        binding.slideToStartBtn.gone()
                        val intent = Intent(this, OnGoingJobDetailsActivity::class.java)
                        startActivity(intent)
                        finish()
                        mStartJobViewModel.navigationComplete()
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

    private fun getDirection(lat: String, long: String){
        val gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+long+ "&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun showCancelDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.cancel_job_layout)

        val yes = dialog.findViewById<TextView>(R.id.yes_btn)
        val no = dialog.findViewById<TextView>(R.id.no_btn)

        yes.setOnClickListener {
            dialog.dismiss()
        }
        no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show()
    }

}