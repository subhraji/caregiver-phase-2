package com.example.caregiverphase2.ui.activity

import android.content.Intent
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
import com.example.caregiverphase2.databinding.ActivityChooseLoginRegBinding
import com.example.caregiverphase2.databinding.ActivityCompletedJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.AgencyFragment
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetCompleteJobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import visible

@AndroidEntryPoint
class CompletedJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompletedJobDetailsBinding

    private val mGetCompleteJobsViewModel: GetCompleteJobsViewModel by viewModels()
    private lateinit var accessToken: String
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCompletedJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            id = intent?.getIntExtra("id",0)!!
        }

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

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

        binding.chatCard.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        binding.viewProfileHtv.setOnClickListener {
            /*val intent = Intent(this,AgencyProfileActivity::class.java)
            intent.putExtra("id",job_id.toString())
            startActivity(intent)*/
            val addPhotoBottomDialogFragment: AgencyFragment =
                AgencyFragment.newInstance()
            val bundle = Bundle()
            bundle.putString("id", id.toString())
            addPhotoBottomDialogFragment.arguments = bundle
            addPhotoBottomDialogFragment.show(
                supportFragmentManager,
                "agency_profile_fragment"
            )
        }

        //observer
        getCompletedJobObserver()

        if(isConnectedToInternet()){
            binding.mainLay.gone()
            binding.detailsShimmerView.visible()
            binding.detailsShimmerView.startShimmer()
            mGetCompleteJobsViewModel.getCompleteJob(token = accessToken, id = id)
        }else{
            Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
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

    private fun getCompletedJobObserver(){
        mGetCompleteJobsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.mainLay.visible()
                    binding.detailsShimmerView.gone()
                    binding.detailsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        //Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        var gen = ""
                        for(i in outcome.data!!.data[0].care_items){
                            if(gen.isEmpty()){
                                gen = i.gender+": "+i.age+" Yrs"
                            }else{
                                gen = gen+", "+i.gender+": "+i.age+" Yrs"
                            }
                        }
                        binding.ageTv.text = gen
                        binding.titleTv.text = outcome.data!!.data[0].title
                        binding.careTypeTv.text = outcome.data!!.data[0].care_type
                        binding.locTv.text = outcome.data!!.data[0].address
                        binding.dateTv.text = outcome.data!!.data[0].start_date.toString()+" to "+outcome.data!!.data[0].end_date.toString()
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
                        }else{
                            binding.noCheckListTv.visible()
                        }
                        mGetCompleteJobsViewModel.navigationComplete()
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
            adapter = BulletPointAdapter(list,this@CompletedJobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@CompletedJobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@CompletedJobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@CompletedJobDetailsActivity)
        }
    }
}