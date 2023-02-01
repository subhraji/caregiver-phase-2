package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAddBioBinding
import com.example.caregiverphase2.databinding.ActivityAskLocationBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.AddBioViewModel
import com.example.caregiverphase2.viewmodel.UpdateLocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import isConnectedToInternet
import loadingDialog

@AndroidEntryPoint
class AddBioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBioBinding

    private val mAddBioViewModel: AddBioViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.clearBtn.setOnClickListener {
            finish()
        }

        binding.addBtn.setOnClickListener {
            val bio = binding.bioTxt.text.toString()
            if(!bio.isEmpty()){
                if(isConnectedToInternet()){
                    mAddBioViewModel.addBio(bio,accessToken)
                    loader.show()
                }else{
                    Toast.makeText(this,"Oops !! No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please provide the bio.", Toast.LENGTH_SHORT).show()
            }
        }

        //observer
        addBioObserver()
    }

    private fun addBioObserver(){
        mAddBioViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        finish()
                        mAddBioViewModel.navigationComplete()
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

}