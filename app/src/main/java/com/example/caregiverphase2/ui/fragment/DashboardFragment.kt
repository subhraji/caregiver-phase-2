package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.DashOpenBidAdapter
import com.example.caregiverphase2.adapter.DashQuickCallsAdapter
import com.example.caregiverphase2.databinding.FragmentDashboardBinding
import com.example.caregiverphase2.databinding.FragmentLoginBinding
import com.example.caregiverphase2.model.TestModel
import java.util.ArrayList

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