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
import com.example.caregiverphase2.adapter.CompletedJobsAdapter
import com.example.caregiverphase2.adapter.UpcommingJobsAdapter
import com.example.caregiverphase2.databinding.FragmentCompleteJobBinding
import com.example.caregiverphase2.databinding.FragmentUpcommingJobBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_complete_job.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetBiddedJobsViewModel
import com.example.caregiverphase2.viewmodel.GetCompleteJobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import visible

@AndroidEntryPoint
class CompleteJobFragment : Fragment() {
    private var _binding: FragmentCompleteJobBinding? = null
    private val binding get() = _binding!!
    private val mGetCompleteJobsViewModel: GetCompleteJobsViewModel by viewModels()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompleteJobBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        binding.completedJobsRecycler.gone()
        binding.noDataLottie.gone()
        binding.jobsShimmerView.startShimmer()

        //observe
        getCompleteJobJobsObserver()


        if(requireActivity().isConnectedToInternet()){
            mGetCompleteJobsViewModel.getCompleteJob(accessToken,0)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getCompleteJobJobsObserver(){
        mGetCompleteJobsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.jobsShimmerView.stopShimmer()
                        binding.jobsShimmerView.gone()
                        if(outcome.data?.data!!.isNotEmpty() && outcome.data?.data != null){
                            binding.completedJobsRecycler.visible()
                            binding.noDataLottie.gone()
                            fillCompletedJobsRecycler(outcome.data?.data!!)
                        }else{
                            binding.completedJobsRecycler.gone()
                            binding.noDataLottie.visible()
                        }
                        mGetCompleteJobsViewModel.navigationComplete()
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

    private fun fillCompletedJobsRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.completedJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = CompletedJobsAdapter(list,requireActivity())
        }
    }


}