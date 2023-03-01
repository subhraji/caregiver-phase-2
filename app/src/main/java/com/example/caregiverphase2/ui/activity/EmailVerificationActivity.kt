package com.example.caregiverphase2.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityChangePasswordBinding
import com.example.caregiverphase2.databinding.ActivityEmailVerificationBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.ChangePasswordViewModel
import com.example.caregiverphase2.viewmodel.SignUpEmailVerificationViewModel
import com.example.caregiverphase2.viewmodel.SignUpViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
    private lateinit var token: String
    private var CHANNEL_ID = "101"

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
            name = extras.getString("name").toString()
            email = extras.getString("email").toString()
            password = extras.getString("password").toString()
            con_password = extras.getString("con_password").toString()
        }

        createNotificationChannel()
        getToken()
        subscribeToTopic()

        loader = this.loadingDialog()

        startTimer()
        binding.resendTv.gone()

        binding.backBtn.setOnClickListener {
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
                        con_password,
                        token
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
                                PrefManager.setUserFullName(name)
                                val intent = Intent(this, AskLocationActivity::class.java)
                                intent.putExtra("from","login")
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    //notification subscribe
    private fun subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("cloud")
            .addOnCompleteListener { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
            }
    }

    //get token
    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log and toast
            //val msg = getString(R.string.msg_token_fmt, token)
            Log.e("Token", token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

    private fun createNotificationChannel() {

        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("textTitle")
            .setContentText("textContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "firebaseNotifChannel"
            val descriptionText = "this is a channel to receive firebase notification."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}