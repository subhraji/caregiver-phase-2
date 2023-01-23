package com.example.caregiverphase2.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentLoginBinding
import com.example.caregiverphase2.databinding.FragmentProfileBinding
import com.example.caregiverphase2.ui.activity.AddBioActivity
import com.example.caregiverphase2.ui.activity.AddCertificateActivity
import com.example.caregiverphase2.ui.activity.AddEducationActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1527980965255-d3b416303d12?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=580&q=80") // image url
            .placeholder(R.color.dash_yellow) // any placeholder to load at start
            .centerCrop()
            .into(binding.userImgView)

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
}