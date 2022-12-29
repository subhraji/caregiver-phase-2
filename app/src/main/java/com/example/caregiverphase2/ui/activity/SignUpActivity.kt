package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityChooseLoginRegBinding
import com.example.caregiverphase2.databinding.ActivitySignUpBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.SignUpEmailVerificationViewModel
import com.example.caregiverphase2.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import hideSoftKeyboard
import loadingDialog
import showKeyboard

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mSignUpEmailVerificationViewModel: SignUpEmailVerificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //listener
        fullNameFocusListener()
        emailFocusListener()
        passwordFocusListener()
        conPasswordFocusListener()

        //observer
        getOtpObserver()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.registerBtn.setOnClickListener {
            /*val intent = Intent(this, AskLocationActivity::class.java)
            startActivity(intent)*/

            val validFullName = binding.fullNameTxtLay.helperText == null && binding.fullNameTxt.text.toString().isNotEmpty()
            val validEmail = binding.emailAddressTxtLay.helperText == null && binding.emailAddressTxt.text.toString().isNotEmpty()
            val validPassword = binding.passwordTxtLay.helperText == null && binding.passwordTxt.text.toString().isNotEmpty()
            val validConPassword = binding.conPasswordTxtLay.helperText == null && binding.conPasswordTxt.text.toString().isNotEmpty()

            if(validFullName){
                if(validEmail){
                    if(validPassword){
                        if(validConPassword){
                            mSignUpEmailVerificationViewModel.getOtp(
                                binding.emailAddressTxt.text.toString(),
                            )
                            loader = this.loadingDialog()
                            loader.show()
                            hideSoftKeyboard()
                        }else{
                            if(binding.conPasswordTxtLay.helperText == null) binding.conPasswordTxtLay.helperText = "required"
                            Toast.makeText(this,binding.conPasswordTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                            binding.conPasswordTxt.showKeyboard()
                        }
                    }else{
                        if(binding.passwordTxtLay.helperText == null) binding.passwordTxtLay.helperText = "required"
                        Toast.makeText(this,binding.passwordTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                        binding.passwordTxt.showKeyboard()
                    }
                }else{
                    if(binding.emailAddressTxtLay.helperText == null) binding.emailAddressTxtLay.helperText = "required"
                    Toast.makeText(this,binding.emailAddressTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                    binding.emailAddressTxt.showKeyboard()
                }
            }else{
                if(binding.fullNameTxtLay.helperText == null) binding.fullNameTxtLay.helperText = "required"
                Toast.makeText(this,binding.fullNameTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                binding.fullNameTxt.showKeyboard()
            }
        }
    }

    private fun fullNameFocusListener(){
        binding.fullNameTxt.doOnTextChanged { text, start, before, count ->
            binding.fullNameTxtLay.helperText = validFullName()
        }
    }

    private fun validFullName(): String? {
        val nameText = binding.fullNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide full name."
        }
        return null
    }

    private fun emailFocusListener(){
        binding.emailAddressTxt.doOnTextChanged { text, start, before, count ->
            binding.emailAddressTxtLay.helperText = validEmail()
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailAddressTxt.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }

        return null
    }

    private fun passwordFocusListener() {
        binding.passwordTxt.doOnTextChanged { text, start, before, count ->
            binding.passwordTxtLay.helperText = validPassword()
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordTxt.text.toString()
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
            binding.conPasswordTxtLay.helperText = validConPassword()
        }
    }
    private fun validConPassword(): String? {
        val new_passwordText = binding.passwordTxt.text.toString()
        val con_passwordText = binding.conPasswordTxt.text.toString()

        if(con_passwordText != new_passwordText){
            return "Password mismatch with the new password."
        }
        return  null
    }

    private fun getOtpObserver(){
        mSignUpEmailVerificationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        val intent = Intent(this, EmailVerificationActivity::class.java)
                        intent.putExtra("name",binding.fullNameTxt.text.toString())
                        intent.putExtra("email",binding.emailAddressTxt.text.toString())
                        intent.putExtra("password",binding.passwordTxt.text.toString())
                        intent.putExtra("con_password",binding.conPasswordTxt.text.toString())
                        startActivity(intent)
                        finish()

                        mSignUpEmailVerificationViewModel.navigationComplete()
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