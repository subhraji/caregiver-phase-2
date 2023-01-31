package com.example.caregiverphase2.ui.fragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.DashQuickCallsAdapter
import com.example.caregiverphase2.adapter.GetBiddedJobsAdapter
import com.example.caregiverphase2.databinding.FragmentBidedJobBinding
import com.example.caregiverphase2.databinding.FragmentClosedJobBinding
import com.example.caregiverphase2.model.pojo.get_bidded_jobs.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.AskLocationActivity
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetBiddedJobsViewModel
import com.example.caregiverphase2.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import visible

@AndroidEntryPoint
class BidedJobFragment : Fragment() {
    private var _binding: FragmentBidedJobBinding? = null
    private val binding get() = _binding!!

    private val mGetBiddedJobsViewModel: GetBiddedJobsViewModel by viewModels()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBidedJobBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.biddedJobsRecycler.gone()
        binding.jobsShimmerView.startShimmer()


        //observer
        getBiddedJobsObserver()

        if(requireActivity().isConnectedToInternet()){
            mGetBiddedJobsViewModel.getBiddedJobs(accessToken,0)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getBiddedJobsObserver(){
        mGetBiddedJobsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.jobsShimmerView.stopShimmer()
                        binding.jobsShimmerView.gone()
                        if(outcome.data?.data!!.isNotEmpty() && outcome.data?.data != null){
                            binding.biddedJobsRecycler.visible()
                            fillBiddedJobsRecycler(outcome.data?.data!!)
                        }else{
                            binding.biddedJobsRecycler.gone()
                        }
                        mGetBiddedJobsViewModel.navigationComplete()
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

    private fun fillBiddedJobsRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.biddedJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = GetBiddedJobsAdapter(list,requireActivity())
        }
    }
}