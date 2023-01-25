package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.GetBiddedJobsAdapter
import com.example.caregiverphase2.adapter.UpcommingJobsAdapter
import com.example.caregiverphase2.databinding.FragmentSettingsBinding
import com.example.caregiverphase2.databinding.FragmentUpcommingJobBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_bidded_jobs.Data

class UpcommingJobFragment : Fragment() {
    private var _binding: FragmentUpcommingJobBinding? = null
    private val binding get() = _binding!!

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

        val list:MutableList<TestModel> = mutableListOf()
        list.add(TestModel("a"))
        list.add(TestModel("a"))
        list.add(TestModel("a"))
        fillUpcommingJobsRecycler(list)
    }

    private fun fillUpcommingJobsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.upcommingJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = UpcommingJobsAdapter(list,requireActivity())
        }
    }
}