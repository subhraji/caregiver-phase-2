package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityChangePasswordBinding
import com.example.caregiverphase2.databinding.ActivityEmailVerificationBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.ChangePasswordViewModel
import com.example.caregiverphase2.viewmodel.SignUpEmailVerificationViewModel
import com.example.caregiverphase2.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import hideSoftKeyboard
import isConnectedToInternet
import loadingDialog
import showKeyboard
import visible

@AndroidEntryPoint
class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerificationBinding
    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mSignUpEmailVerificationViewModel: SignUpEmailVerificationViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by viewModels()

    private var name: String = ""
    private var email: String = ""
    private var password: String = ""
    private var con_password: String = ""
    var cTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val extras = intent.extras
        if (extras != null) {
            name = extras.getString("company_name").toString()
            email = extras.getString("email").toString()
            password = extras.getString("password").toString()
            con_password = extras.getString("con_password").toString()
        }

        loader = this.loadingDialog()

        startTimer()
        binding.resendTv.gone()

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, ChooseLoginRegActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.edTxt1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt1.text.length == 1){
                    binding.edTxt2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt2.text.length == 1){
                    binding.edTxt3.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt3.text.length == 1){
                    binding.edTxt4.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt4.text.length == 1){
                    binding.edTxt5.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edTxt5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(binding.edTxt5.text.length == 1){
                    binding.edTxt6.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.verifyBtn.setOnClickListener {
            val otp = "${binding.edTxt1.text}${binding.edTxt2.text}${binding.edTxt3.text}${binding.edTxt4.text}${binding.edTxt5.text}${binding.edTxt6.text}"
            hideSoftKeyboard()
            if(otp.length == 6){
                if(isConnectedToInternet()){
                    signUpViewModel.signup(
                        otp.toString().toInt(),
                        name,
                        email,
                        password,
                        con_password
                    )
                    loader.show()

                }else{
                    Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Invalid OTP.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resendTv.setOnClickListener {
            if(isConnectedToInternet()){
                mSignUpEmailVerificationViewModel.getOtp(
                    email
                )
                loader = this.loadingDialog()
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
            }

        }

        //observer
        signObserver()
        getOtpObserver()
    }


    fun startTimer() {
        cTimer = object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerTv.setText("OTP well be expired in: " + millisUntilFinished / 1000 +" sec");
            }
            override fun onFinish() {
                cancelTimer()
                binding.resendTv.visible()
            }
        }
        (cTimer as CountDownTimer).start()
    }

    fun cancelTimer() {
        if (cTimer != null) cTimer!!.cancel()
    }

    private fun getOtpObserver(){
        mSignUpEmailVerificationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        startTimer()
                        binding.resendTv.gone()

                        binding.edTxt1.text = null
                        binding.edTxt2.text = null
                        binding.edTxt3.text = null
                        binding.edTxt4.text = null
                        binding.edTxt5.text = null
                        binding.edTxt6.text = null
                        binding.edTxt1.showKeyboard()

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

    private fun signObserver(){
        signUpViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if (outcome.data!!.token != null) {
                            outcome.data!!.token?.let {
                                PrefManager.setKeyAuthToken(it)

                                val intent = Intent(this, AskLocationActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        PrefManager.setLogInStatus(true)

                        signUpViewModel.navigationComplete()
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