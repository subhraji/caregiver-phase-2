package com.example.caregiverphase2.ui.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.BulletPointAdapter
import com.example.caregiverphase2.adapter.CheckListAdapter
import com.example.caregiverphase2.databinding.ActivityOnGoingJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.CompleteJobViewModel
import com.example.caregiverphase2.viewmodel.GetOngoingJobViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ncorti.slidetoact.SlideToActView
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class OnGoingJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnGoingJobDetailsBinding
    private val mGetOngoingJobViewModel: GetOngoingJobViewModel by viewModels()
    private val mCompleteJobViewModel: CompleteJobViewModel by viewModels()

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private var checkList:MutableList<String> = mutableListOf()
    private var job_id: Int = 0
    private var lat: String = ""
    private var long: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOnGoingJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        //swipe
        binding.slideToCompleteBtn.onSlideCompleteListener = (object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                binding.slideToCompleteBtn.resetSlider()
            }
        })

        binding.slideToCompleteBtn.onSlideToActAnimationEventListener = (object : SlideToActView.OnSlideToActAnimationEventListener{
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
                if(!checkList.isEmpty()){
                    showCheckListBottomsheet()
                }else{
                    // complete api call
                    mCompleteJobViewModel.completeJob(job_id,accessToken)
                    loader.show()
                }
            }

        })

        //observer
        getOngoingJobObserver()
        completeJobObserver()

        if(isConnectedToInternet()){
            binding.mainLay.gone()
            binding.detailsShimmerView.visible()
            binding.detailsShimmerView.startShimmer()
            mGetOngoingJobViewModel.getOngoingJob(accessToken)
        }else{
            Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
        }

        binding.getDirectionBtn.setOnClickListener {
            if(!lat.isEmpty() && !long.isEmpty()){
                getDirection(lat, long)
            }else{
                Toast.makeText(this,"Could not fetch the location ${lat}...${long}",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getOngoingJobObserver(){
        mGetOngoingJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data!!.data.isNotEmpty()){
                            binding.mainLay.visible()
                            binding.detailsShimmerView.gone()
                            binding.detailsShimmerView.stopShimmer()

                            job_id = outcome.data!!.data[0].job_id

                            var gen = ""
                            for(i in outcome.data!!.data[0].care_items){
                                if(gen.isEmpty()){
                                    gen = i.gender+": "+i.age
                                }else{
                                    gen = gen+", "+i.gender+": "+i.age
                                }
                            }
                            binding.ageTv.text = gen
                            binding.titleTv.text = outcome.data!!.data[0].title
                            binding.careTypeTv.text = outcome.data!!.data[0].care_type
                            binding.locTv.text = outcome.data!!.data[0].short_address
                            binding.dateTv.text = outcome.data!!.data[0].start_date.toString()+"-"+outcome.data!!.data[0].end_date.toString()
                            binding.timeTv.text = outcome.data!!.data[0].start_time.toString()+" - "+outcome.data!!.data[0].end_time.toString()
                            binding.priceTv.text = "$"+outcome.data!!.data[0].amount.toString()
                            binding.agencyNameTv.text = outcome.data!!.data[0].agency_name.toString()
                            binding.jobDescTv.text = outcome.data!!.data[0].description.toString()
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+outcome.data!!.data[0].agency_photo) // image url
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
                                checkList = outcome.data!!.data[0].check_list.toMutableList()
                            }else{
                                binding.noCheckListTv.visible()
                            }

                            outcome.data!!.data[0].lat?.let {
                                outcome.data!!.data[0].long?.let {
                                    binding.getDirectionBtn.visible()
                                    lat = outcome.data!!.data[0].lat
                                    long = outcome.data!!.data[0].long
                                }
                            }

                        }else{
                            binding.mainLay.gone()
                            binding.detailsShimmerView.visible()
                            binding.detailsShimmerView.startShimmer()
                        }
                        mGetOngoingJobViewModel.navigationComplete()
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
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun showCheckListBottomsheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.check_list_bottom_sheet_layout, null)

        val checkRecycler = view.findViewById<RecyclerView>(R.id.check_recycler)
        val submit = view.findViewById<CardView>(R.id.submit_btn)

        submit.setOnClickListener {
            mCompleteJobViewModel.completeJob(job_id,accessToken)
            loader.show()
            dialog.dismiss()
        }

        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        checkRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = CheckListAdapter(checkList,this@OnGoingJobDetailsActivity)
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showCompleteDialog(title: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.job_accept_success_dialog_layout)

        val okay = dialog.findViewById<TextView>(R.id.ok_btn)
        val title_tv = dialog.findViewById<TextView>(R.id.text_view_2)

        title_tv.text = title
        /*Handler(Looper.getMainLooper()).postDelayed({

        }, 2500)*/
        okay.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
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

    private fun completeJobObserver(){
        mCompleteJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        showCompleteDialog("Your job has been completed successfully, You can find this on the completed job section.")
                        mCompleteJobViewModel.navigationComplete()
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

    private fun getDirection(lat: String, long: String){
        val gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+long+ "&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}