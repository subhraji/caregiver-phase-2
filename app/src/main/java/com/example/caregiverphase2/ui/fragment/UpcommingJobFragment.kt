package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.GetBiddedJobsAdapter
import com.example.caregiverphase2.adapter.UpcommingJobsAdapter
import com.example.caregiverphase2.databinding.FragmentSettingsBinding
import com.example.caregiverphase2.databinding.FragmentUpcommingJobBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_bidded_jobs.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetUpcommingJobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import visible

@AndroidEntryPoint
class UpcommingJobFragment : Fragment() {
    private var _binding: FragmentUpcommingJobBinding? = null
    private val binding get() = _binding!!
    private val mGetUpcommingJobsViewModel: GetUpcommingJobsViewModel by viewModels()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpcommingJobBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.upcommingJobsRecycler.gone()
        binding.jobsShimmerView.visible()
        binding.jobsShimmerView.startShimmer()
        if(requireActivity().isConnectedToInternet()){
            mGetUpcommingJobsViewModel.getUpcommingJobs(accessToken,"upcoming")
        }else{
            Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
        }

        //observer
        getUpcommingJoobsObserver()
    }

    private fun fillUpcommingJobsRecycler(list: List<com.example.caregiverphase2.model.pojo.upcomming_job.Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.upcommingJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = UpcommingJobsAdapter(list,requireActivity())
        }
    }

    private fun getUpcommingJoobsObserver(){
        mGetUpcommingJobsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.jobsShimmerView.gone()
                        binding.jobsShimmerView.stopShimmer()
                        if(outcome.data?.data!!.isNotEmpty()){
                            binding.upcommingJobsRecycler.visible()
                            fillUpcommingJobsRecycler(outcome.data!!.data)
                        }else{
                            binding.upcommingJobsRecycler.gone()
                        }
                        mGetUpcommingJobsViewModel.navigationComplete()
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