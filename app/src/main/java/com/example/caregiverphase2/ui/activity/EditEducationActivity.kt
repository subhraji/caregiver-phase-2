package com.example.caregiverphase2.ui.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.EditEducationItemAdapter
import com.example.caregiverphase2.adapter.ProfileEducationAdapter
import com.example.caregiverphase2.databinding.ActivityEarningsBinding
import com.example.caregiverphase2.databinding.ActivityEditEducationBinding
import com.example.caregiverphase2.model.pojo.get_profile.Education
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.DeleteDocClickListener
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.DeleteCertificateViewModel
import com.example.caregiverphase2.viewmodel.DeleteEducationViewModel
import com.example.caregiverphase2.viewmodel.GetProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class EditEducationActivity : AppCompatActivity(), DeleteDocClickListener {
    private lateinit var binding: ActivityEditEducationBinding
    private val mGetProfileViewModel: GetProfileViewModel by viewModels()
    private val mDeleteEducationViewModel: DeleteEducationViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditEducationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.backBtn.setOnClickListener {
            finish()
        }

        //observe
        getEducationObserver()
        deleteEducationObserver()
    }

    override fun onResume() {
        super.onResume()
        binding.educationRecycler.gone()
        binding.educationShimmerView.visible()
        binding.educationShimmerView.startShimmer()
        if(isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
        }else{
            Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fillEducationRecycler(list: List<Education>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.educationRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = EditEducationItemAdapter(list,this@EditEducationActivity, this@EditEducationActivity)
        }
    }

    private fun getEducationObserver(){
        mGetProfileViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.educationShimmerView.gone()
                        binding.educationShimmerView.stopShimmer()
                        if(outcome.data?.data?.education != null && outcome.data?.data?.education!!.isNotEmpty()){
                            binding.educationRecycler.visible()
                            fillEducationRecycler(outcome.data?.data?.education!!)
                        }else{
                            binding.educationRecycler.gone()
                        }
                        mGetProfileViewModel.navigationComplete()
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

    private fun deleteEducationObserver(){
        mDeleteEducationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        binding.educationRecycler.gone()
                        binding.educationShimmerView.visible()
                        binding.educationShimmerView.startShimmer()
                        if(isConnectedToInternet()){
                            mGetProfileViewModel.getProfile(accessToken)
                        }else{
                            Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
                        }
                        mDeleteEducationViewModel.navigationComplete()
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

    override fun deleteDoc(id: Int, category: String) {
        showDeletePopUp(id, category)
    }

    private fun showDeletePopUp(id: Int, type: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete")
        builder.setMessage("Do you want to delete the ${type} ?")
        builder.setIcon(R.drawable.ic_baseline_delete_outline_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(isConnectedToInternet()){
                mDeleteEducationViewModel.deleteEducation(id.toString(), accessToken)
                loader.show()
            }else{
                Toast.makeText(this,"Oops! No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}