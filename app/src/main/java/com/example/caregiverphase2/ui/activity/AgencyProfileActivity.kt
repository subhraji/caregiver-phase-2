package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAgencyProfileBinding
import com.example.caregiverphase2.databinding.ActivityAskLocationBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetAgencyProfileViewModel
import com.example.caregiverphase2.viewmodel.GetBiddedJobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import isConnectedToInternet
import loadingDialog

@AndroidEntryPoint
class AgencyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgencyProfileBinding
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mGetAgencyProfileViewModel: GetAgencyProfileViewModel by viewModels()
    private lateinit var accessToken: String
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgencyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            id = intent?.getStringExtra("id")!!
        }

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        //observer
        getAgencyProfileObserve()

        if(isConnectedToInternet()){
            mGetAgencyProfileViewModel.getAgencyProfile(accessToken,id!!)
            loader.show()
        }else{
            Toast.makeText(this,"Oops!! no internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAgencyProfileObserve(){
        mGetAgencyProfileViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        outcome.data?.data?.years_in_business?.let {
                            binding.yearsOfBusinessTv.text = it.toString()
                        }
                        outcome.data?.data?.country.let {
                            binding.countryTv.text = it.toString()
                        }
                        outcome.data?.data?.phone?.let {
                            binding.taxIdTv.text = it.toString()
                        }
                        outcome.data?.data?.legal_structure?.let {
                            binding.lsTv.text = it.toString()
                        }
                        outcome.data?.data?.about_company?.let {
                            binding.backgroundTv.text = it.toString()
                        }
                        outcome.data?.data?.company_name?.let {
                            binding.nameTv.text = it.toString()
                        }
                        outcome.data?.data?.email?.let {
                            binding.emailTv.text = it.toString()
                        }
                        Glide.with(this).load(Constants.PUBLIC_URL+ outcome.data!!.data.photo)
                            .placeholder(R.color.color_grey)
                            .into(binding.profileImg)
                        mGetAgencyProfileViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    loader.dismiss()
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}