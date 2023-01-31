package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.ClosedJobsAdapter
import com.example.caregiverphase2.adapter.CompletedJobsAdapter
import com.example.caregiverphase2.databinding.FragmentClosedJobBinding
import com.example.caregiverphase2.databinding.FragmentCompleteJobBinding
import com.example.caregiverphase2.model.TestModel

class ClosedJobFragment : Fragment() {
    private var _binding: FragmentClosedJobBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClosedJobBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list:MutableList<TestModel> = mutableListOf()
        list.add(TestModel("a"))
        list.add(TestModel("a"))
        list.add(TestModel("a"))
        fillClosedJobsRecycler(list)
    }

    private fun fillClosedJobsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.closedJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = ClosedJobsAdapter(list,requireActivity())
        }
    }
}