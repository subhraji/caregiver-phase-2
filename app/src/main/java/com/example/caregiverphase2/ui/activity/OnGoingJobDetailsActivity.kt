package com.example.caregiverphase2.ui.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.BulletPointAdapter
import com.example.caregiverphase2.databinding.ActivityOnGoingJobDetailsBinding
import com.example.caregiverphase2.databinding.ActivityUpcommingJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetOngoingJobViewModel
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
    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog

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
                showCompleteDialog("Job is completed successfully, you can find this job on complete job section.")
            }

        })

        //observer
        getOngoingJobObserver()

        if(isConnectedToInternet()){
            binding.mainLay.gone()
            binding.detailsShimmerView.visible()
            binding.detailsShimmerView.startShimmer()
            mGetOngoingJobViewModel.getOngoingJob(accessToken)
        }else{
            Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
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
                            binding.dateTv.text = outcome.data!!.data[0].date.toString()
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
                            if(outcome.data!!.data[0].experties.isNotEmpty()){
                                binding.jobExpRecycler.visible()
                                binding.jobExpHtv.visible()
                                jobExpFillRecycler(outcome.data!!.data[0].experties.toMutableList())
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

}