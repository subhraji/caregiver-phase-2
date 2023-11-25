package com.example.caregiverphase2.ui.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.EditCertificateItemAdapter
import com.example.caregiverphase2.adapter.ShowCertificateAdapter
import com.example.caregiverphase2.databinding.ActivityEditCertificateBinding
import com.example.caregiverphase2.databinding.ActivityEditEducationBinding
import com.example.caregiverphase2.model.pojo.get_profile.Certificate
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.DeleteDocClickListener
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.DeleteCertificateViewModel
import com.example.caregiverphase2.viewmodel.EditEducationViewModel
import com.example.caregiverphase2.viewmodel.GetProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class EditCertificateActivity : AppCompatActivity(), DeleteDocClickListener {
    private lateinit var binding: ActivityEditCertificateBinding
    private val mGetProfileViewModel: GetProfileViewModel by viewModels()
    private val mDeleteCertificateViewModel: DeleteCertificateViewModel by viewModels()
    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.backBtn.setOnClickListener {
            finish()
        }

        //observe
        getCertificateObserver()
        deleteCertificateObserver()
    }

    override fun onResume() {
        super.onResume()
        binding.certificateRecycler.gone()
        binding.educationShimmerView.visible()
        binding.educationShimmerView.startShimmer()
        if(isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fillCertificateRecycler(list: List<Certificate>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.certificateRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = EditCertificateItemAdapter(list,this@EditCertificateActivity, this@EditCertificateActivity)
        }
    }

    private fun getCertificateObserver(){
        mGetProfileViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.educationShimmerView.gone()
                        binding.educationShimmerView.stopShimmer()
                        if(outcome.data?.data?.certificate != null && outcome.data?.data?.certificate!!.isNotEmpty()){
                            binding.certificateRecycler.visible()
                            fillCertificateRecycler(outcome.data?.data?.certificate!!)
                        }else{
                            binding.certificateRecycler.gone()
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

    private fun deleteCertificateObserver(){
        mDeleteCertificateViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        binding.certificateRecycler.gone()
                        binding.educationShimmerView.visible()
                        binding.educationShimmerView.startShimmer()
                        if(isConnectedToInternet()){
                            mGetProfileViewModel.getProfile(accessToken)
                        }else{
                            Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
                        }
                        mDeleteCertificateViewModel.navigationComplete()
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
                mDeleteCertificateViewModel.deleteCertificate(id.toString(), accessToken)
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