package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.CompletedJobsAdapter
import com.example.caregiverphase2.adapter.UpcommingJobsAdapter
import com.example.caregiverphase2.databinding.FragmentCompleteJobBinding
import com.example.caregiverphase2.databinding.FragmentUpcommingJobBinding
import com.example.caregiverphase2.model.TestModel

class CompleteJobFragment : Fragment() {
    private var _binding: FragmentCompleteJobBinding? = null
    private val binding get() = _binding!!

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

        val list:MutableList<TestModel> = mutableListOf()
        list.add(TestModel("a"))
        list.add(TestModel("a"))
        list.add(TestModel("a"))
        fillCompletedJobsRecycler(list)
    }

    private fun fillCompletedJobsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.completedJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = CompletedJobsAdapter(list,requireActivity())
        }
    }
}