package com.example.caregiverphase2.ui.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentForgotPassChangeBinding
import com.example.caregiverphase2.databinding.FragmentForgotPassOTPBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.AuthActivity
import com.example.caregiverphase2.viewmodel.ChangeForgotPassViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import loadingDialog
import showKeyboard

@AndroidEntryPoint
class ForgotPassChangeFragment : Fragment() {
    private var _binding: FragmentForgotPassChangeBinding? = null
    private val binding get() = _binding!!

    private val mChangeForgotPassViewModel: ChangeForgotPassViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    private lateinit var email_id: String
    private lateinit var token: String
    private var CHANNEL_ID = "101"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email_id = it.getString("email")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPassChangeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = requireActivity().loadingDialog()

        //validation
        newPasswordFocusListener()
        conPasswordFocusListener()

        createNotificationChannel()
        getToken()
        subscribeToTopic()

        //observe
        changePassObserver()

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.updateBtn.setOnClickListener {
            val validNewPass = binding.newPassTxtLay.helperText == null && binding.newPasswordTxt.text.toString().isNotEmpty()
            val validConPass = binding.conPassTxtLay.helperText == null && binding.conPasswordTxt.text.toString().isNotEmpty()

            if(validNewPass){
                if(validConPass){
                    mChangeForgotPassViewModel.changeForgotPass(
                        email_id,
                        binding.newPasswordTxt.text.toString(),
                        binding.conPasswordTxt.text.toString(),
                        token
                    )
                    loader.show()
                }else{
                    if(binding.conPassTxtLay.helperText == null) binding.conPassTxtLay.helperText = "required"
                    Toast.makeText(requireActivity(),binding.conPassTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                    binding.conPasswordTxt.showKeyboard()
                }
            }else{
                if(binding.newPassTxtLay.helperText == null) binding.newPassTxtLay.helperText = "required"
                Toast.makeText(requireActivity(),binding.newPassTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                binding.newPasswordTxt.showKeyboard()
            }
        }
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
            return "Password Must contain 1 upper case character."
        }
        if(!passwordText.matches(".*[a-z].*".toRegex())){
            return "Password Must contain 1 lower case character."
        }
        if(!passwordText.matches(".*[0-9].*".toRegex())){
            return "Password Must contain at least 1 number."
        }
        if(!passwordText.matches(".*[!@$#%&*_-].*".toRegex())){
            return "Password Must contain 1 special character (!@$#%&*_-)."
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

    private fun changePassObserver(){
        mChangeForgotPassViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(requireActivity(),outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        val intent = Intent(requireActivity(), AuthActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                        mChangeForgotPassViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()
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

        NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
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
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}