package com.example.caregiverphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentClosedJobBinding
import com.example.caregiverphase2.databinding.FragmentCompleteJobBinding

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

}