package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityEditBasicInfoBinding
import com.example.caregiverphase2.databinding.ActivityEditCertificateBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.EditBasicInfoViewModel
import com.example.caregiverphase2.viewmodel.EditEducationViewModel
import dagger.hilt.android.AndroidEntryPoint
import hideSoftKeyboard
import isConnectedToInternet
import loadingDialog
import showKeyboard

@AndroidEntryPoint
class EditBasicInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBasicInfoBinding

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mEditBasicInfoViewModel: EditBasicInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditBasicInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            val phone = intent?.getStringExtra("phone")
            val exp = intent?.getStringExtra("experience")

            phone?.let {
                binding.phoneTxt.text = Editable.Factory.getInstance().newEditable(phone)
            }
            exp?.let {
                binding.expTxt.text = Editable.Factory.getInstance().newEditable(exp)
            }
        }

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.updateBtn.setOnClickListener {
            val validPhone = binding.phoneTxtLay.helperText == null && binding.phoneTxt.text.toString().isNotEmpty()

            if(validPhone){
                hideSoftKeyboard()
                if(isConnectedToInternet()){
                    mEditBasicInfoViewModel.editBasicInfo(
                        binding.phoneTxt.text.toString(),
                        binding.expTxt.text.toString(),
                        accessToken
                    )
                    loader.show()
                }else{
                    Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }else{
                if(binding.phoneTxtLay.helperText == null) binding.phoneTxtLay.helperText = "required"
                binding.phoneTxt.showKeyboard()
            }
        }

        //validation
        mobileFocusListener()

        //observe
        editBasicInfoObserver()
    }

    private fun mobileFocusListener(){
        binding.phoneTxt.doOnTextChanged { text, start, before, count ->
            binding.phoneTxtLay.helperText = validMobileNumber()
        }
    }

    private fun validMobileNumber(): String? {
        val mobileText = binding.phoneTxt.text.toString()
        if(mobileText.length != 10){
            return "10 digit number required."
        }

        if(binding.phoneTxt.text.toString().toDouble() == 0.00){
            return "Please provide a valid phone number."
        }

        return  null
    }

    private fun editBasicInfoObserver(){
        mEditBasicInfoViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        mEditBasicInfoViewModel.navigationComplete()
                        finish()
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

}