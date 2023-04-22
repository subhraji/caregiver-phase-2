package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentAgencyBinding
import com.example.caregiverphase2.databinding.FragmentClosedJobBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetAgencyProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import isConnectedToInternet
import loadingDialog

@AndroidEntryPoint
class AgencyFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAgencyBinding? = null
    private val binding get() = _binding!!
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mGetAgencyProfileViewModel: GetAgencyProfileViewModel by viewModels()
    private lateinit var accessToken: String
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getString("id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgencyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = requireActivity().loadingDialog()

        //observer
        getAgencyProfileObserve()

        if(requireActivity().isConnectedToInternet()){
            mGetAgencyProfileViewModel.getAgencyProfile(accessToken,id!!)
            loader.show()
        }else{
            Toast.makeText(requireActivity(),"Oops!! no internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAgencyProfileObserve(){
        mGetAgencyProfileViewModel.response.observe(requireActivity(), Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        outcome.data?.data?.years_in_business?.let {
                            binding.yearsOfBusinessTv.text = it.toString()
                        }
                        outcome.data?.data?.country.let {
                            binding.countryTv.text = it.toString()
                        }
                        outcome.data?.data?.phone?.let {
                            binding.taxIdTv.text = it.toString()
                        }
                        outcome.data?.data?.legal_structure?.let {
                            binding.lsTv.text = it.toString()
                        }
                        outcome.data?.data?.about_company?.let {
                            binding.backgroundTv.text = it.toString()
                        }
                        outcome.data?.data?.company_name?.let {
                            binding.nameTv.text = it.toString()
                        }
                        outcome.data?.data?.email?.let {
                            binding.emailTv.text = it.toString()
                        }
                        Glide.with(requireActivity()).load(Constants.PUBLIC_URL+ outcome.data!!.data.photo)
                            .placeholder(R.color.color_grey)
                            .into(binding.profileImg)
                        mGetAgencyProfileViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    loader.dismiss()
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AgencyFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}