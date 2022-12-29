package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityChangePasswordBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import loadingDialog
import showKeyboard

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mChangePasswordViewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        loader = this.loadingDialog()

        //validation
        newPasswordFocusListener()
        oldPasswordFocusListener()
        conPasswordFocusListener()

        //observer
        changePasswordObserve()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.updatePassBtn.setOnClickListener {
            val validOldPass = binding.oldPassTxtLay.helperText == null && binding.oldPasswordTxt.text.toString().isNotEmpty()
            val validNewPass = binding.newPassTxtLay.helperText == null && binding.newPasswordTxt.text.toString().isNotEmpty()
            val validConPass = binding.conPassTxtLay.helperText == null && binding.conPasswordTxt.text.toString().isNotEmpty()

            if(validOldPass){
                if(validNewPass){
                    if(validConPass){
                        mChangePasswordViewModel.changePassword(
                            binding.oldPasswordTxt.text.toString(),
                            binding.newPasswordTxt.text.toString(),
                            binding.conPasswordTxt.text.toString(),
                            accessToken
                        )
                        loader.show()
                    }else{
                        if(binding.conPassTxtLay.helperText == null) binding.conPassTxtLay.helperText = "required"
                        Toast.makeText(this,binding.conPassTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                        binding.conPasswordTxt.showKeyboard()
                    }
                }else{
                    if(binding.newPassTxtLay.helperText == null) binding.newPassTxtLay.helperText = "required"
                    Toast.makeText(this,binding.newPassTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                    binding.newPasswordTxt.showKeyboard()
                }
            }else{
                if(binding.oldPassTxtLay.helperText == null) binding.oldPassTxtLay.helperText = "required"
                Toast.makeText(this,binding.oldPassTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                binding.oldPasswordTxt.showKeyboard()
            }

        }

    }

    private fun oldPasswordFocusListener() {
        binding.oldPasswordTxt.doOnTextChanged { text, start, before, count ->
            binding.oldPassTxtLay.helperText = validOldPassword()
        }
    }
    private fun validOldPassword(): String? {
        val passwordText = binding.oldPasswordTxt.text.toString()
        if(passwordText.length == 0){
            return "Required."
        }
        return  null
    }

    private fun newPasswordFocusListener() {
        binding.newPasswordTxt.doOnTextChanged { text, start, before, count ->
            binding.newPassTxtLay.helperText = validNewPassword()
        }
    }
    private fun validNewPassword(): String? {
        val passwordText = binding.newPasswordTxt.text.toString()
        if(passwordText.length < 6){
            return "Minimum 6 characters required."
        }
        if(!passwordText.matches(".*[A-Z].*".toRegex())){
            return "Must contain 1 upper case character."
        }
        if(!passwordText.matches(".*[a-z].*".toRegex())){
            return "Must contain 1 lower case character."
        }
        if(!passwordText.matches(".*[0-9].*".toRegex())){
            return "Must contain at least 1 number."
        }
        if(!passwordText.matches(".*[!@$#%&*_-].*".toRegex())){
            return "Must contain 1 special character (!@$#%&*_-)."
        }
        return  null
    }

    private fun conPasswordFocusListener() {
        binding.conPasswordTxt.doOnTextChanged { text, start, before, count ->
            binding.conPassTxtLay.helperText = validConPassword()
        }
    }
    private fun validConPassword(): String? {
        val new_passwordText = binding.newPasswordTxt.text.toString()
        val con_passwordText = binding.conPasswordTxt.text.toString()

        if(con_passwordText != new_passwordText){
            return "Password mismatch with the new password."
        }
        return  null
    }

    private fun changePasswordObserve(){
        mChangePasswordViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mChangePasswordViewModel.navigationComplete()
                        finish()
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