package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentDashboardBinding
import com.example.caregiverphase2.databinding.FragmentForgotPassOTPBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.viewmodel.ForgotPassOtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import hideSoftKeyboard
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class ForgotPassOTPFragment : Fragment() {
    private var _binding: FragmentForgotPassOTPBinding? = null
    private val binding get() = _binding!!

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    var cTimer: CountDownTimer? = null
    private lateinit var email_id: String

    private val mForgotPassOtpViewModel: ForgotPassOtpViewModel by viewModels()

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
        _binding = FragmentForgotPassOTPBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe
        verifyOtpObserve()

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        loader = requireActivity().loadingDialog()
        startTimer()
        binding.resendTv.gone()

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
            requireActivity().hideSoftKeyboard()
            if(otp.length == 6){
                if(requireActivity().isConnectedToInternet()){
                    mForgotPassOtpViewModel.forgotPassSendOtp(email_id, otp)
                    loader.show()
                }else{
                    Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireActivity(),"Invalid OTP.", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun verifyOtpObserve(){
        mForgotPassOtpViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val bundle = Bundle()
                        bundle.putString("email", email_id)
                        findNavController().navigate(
                            R.id.action_forgotPassOTPFragment_to_forgotPassChangeFragment,
                            bundle
                        )
                        mForgotPassOtpViewModel.navigationComplete()
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

}