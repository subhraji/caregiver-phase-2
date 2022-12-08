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
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.DashOpenBidAdapter
import com.example.caregiverphase2.adapter.DashQuickCallsAdapter
import com.example.caregiverphase2.databinding.FragmentDashboardBinding
import com.example.caregiverphase2.databinding.FragmentLoginBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.viewmodel.NewViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quickCallList = ArrayList<TestModel>()
        quickCallList.add(TestModel("a"))
        quickCallList.add(TestModel("b"))
        quickCallList.add(TestModel("c"))
        fillQuickCallsRecycler(quickCallList)
        fillOpenBidsRecycler(quickCallList)
        fillOpenJobsRecycler(quickCallList)

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1527980965255-d3b416303d12?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=580&q=80") // image url
            .placeholder(R.color.dash_yellow) // any placeholder to load at start
            .centerCrop()
            .into(binding.userImageView)

    }

    private fun fillQuickCallsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.quickCallRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashQuickCallsAdapter(list,requireActivity())
        }
    }

    private fun fillOpenBidsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.openBidsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashOpenBidAdapter(list,requireActivity(),true)
        }
    }

    private fun fillOpenJobsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.openJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashOpenBidAdapter(list,requireActivity(),false)
        }
    }
}