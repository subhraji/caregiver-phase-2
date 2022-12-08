package com.example.caregiverphase2.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentLoginBinding
import com.example.caregiverphase2.databinding.FragmentSettingsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.AskLocationActivity
import com.example.caregiverphase2.ui.activity.ChooseLoginRegActivity
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.LoginViewModel
import com.example.caregiverphase2.viewmodel.LogoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import isConnectedToInternet
import loadingDialog

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val mLogoutViewModel: LogoutViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        //observer
        logoutObserve()

        binding.logoutBtn.setOnClickListener {
            showLogoutPopUp()
        }
    }

    private fun showLogoutPopUp(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Logout")
        builder.setMessage("Do you want to exit the app ?")
        builder.setIcon(R.drawable.ic_baseline_logout_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(requireActivity().isConnectedToInternet()){
                mLogoutViewModel.logout(accessToken)
                loader = requireActivity().loadingDialog()
                loader.show()
            }else{
                Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun logoutObserve(){
        mLogoutViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        PrefManager.clearPref()
                        startActivity(Intent(requireActivity(), ChooseLoginRegActivity::class.java))
                        requireActivity().finish()
                        mLogoutViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}