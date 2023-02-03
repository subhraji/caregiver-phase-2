package com.example.caregiverphase2.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.ProfileEducationAdapter
import com.example.caregiverphase2.adapter.ShowCertificateAdapter
import com.example.caregiverphase2.adapter.UpcommingJobsAdapter
import com.example.caregiverphase2.databinding.FragmentLoginBinding
import com.example.caregiverphase2.databinding.FragmentProfileBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_profile.Certificate
import com.example.caregiverphase2.model.pojo.get_profile.Education
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.AddBioActivity
import com.example.caregiverphase2.ui.activity.AddCertificateActivity
import com.example.caregiverphase2.ui.activity.AddEducationActivity
import com.example.caregiverphase2.ui.activity.ChooseLoginRegActivity
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetProfileViewModel
import com.example.caregiverphase2.viewmodel.LogoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val mGetProfileViewModel: GetProfileViewModel by viewModels()
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = requireActivity().loadingDialog()

        //observer
        getProfileObserve()

        binding.addBioBtn.setOnClickListener {
            val intent = Intent(requireActivity(), AddBioActivity::class.java)
            startActivity(intent)
        }

        binding.addEduBtn.setOnClickListener {
            val intent = Intent(requireActivity(), AddEducationActivity::class.java)
            startActivity(intent)
        }

        binding.addCertificateBtn.setOnClickListener {
            val intent = Intent(requireActivity(), AddCertificateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        if(requireActivity().isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
            loader.show()
        }else{
            Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_SHORT).show()
        }

        super.onResume()
    }

    private fun getProfileObserve(){
        mGetProfileViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val data = outcome.data?.data

                        data?.basic_info?.photo?.let {
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+it) // image url
                                .placeholder(R.color.dash_yellow) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.userImgView)
                        }
                        data?.basic_info?.care_completed?.let {
                            binding.careCompletedTv.text = it.toString()
                        }
                        data?.basic_info?.user?.email?.let {
                            binding.emailTv.text = it.toString()
                        }
                        data?.basic_info?.phone?.let {
                            binding.phoneTv.text = it.toString()
                        }
                        data?.basic_info?.experience?.let {
                            binding.expTv.text = it.toString()+" Years"
                        }
                        data?.basic_info?.gender?.let {
                            binding.genderTv.text = it.toString()
                        }
                        data?.basic_info?.bio?.let {
                            binding.showBioTv.text = it.toString()
                        }
                        data?.basic_info?.dob?.let {
                            binding.ageTv.text = it.toString()+" Years"
                        }
                        if(data?.basic_info?.bio != null){
                            binding.addBioBtn.gone()
                            binding.bioImg.gone()
                            binding.bioHtv.gone()
                            binding.showBioHtv.visible()
                            binding.showBioTv.visible()
                            binding.editBioBtn.visible()
                        }else{
                            binding.addBioBtn.visible()
                            binding.bioImg.visible()
                            binding.bioHtv.visible()
                            binding.showBioHtv.gone()
                            binding.showBioTv.gone()
                            binding.editBioBtn.gone()
                        }

                        if(data?.education != null && data.education.isNotEmpty()){
                            binding.educationRecycler.visible()
                            binding.showEducationHtv.visible()
                            binding.eduImg.gone()
                            binding.eduHtv.gone()
                            binding.addEduBtn.gone()
                            fillEducationRecycler(data?.education)
                        }else{
                            binding.educationRecycler.gone()
                            binding.showEducationHtv.gone()
                            binding.eduImg.visible()
                            binding.eduHtv.visible()
                            binding.addEduBtn.visible()
                        }

                        if(data?.education != null && data.education.isNotEmpty()){

                        }else{

                        }

                        mGetProfileViewModel.navigationComplete()
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

    private fun fillEducationRecycler(list: List<Education>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.educationRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = ProfileEducationAdapter(list,requireActivity())
        }
    }

    private fun fillCertificateRecycler(list: List<Certificate>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.educationRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = ShowCertificateAdapter(list,requireActivity())
        }
    }
}