package com.example.caregiverphase2.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentImagePreviewBinding
import com.example.caregiverphase2.utils.UploadDocListener
import java.io.InputStream

class ImagePreviewFragment(uploadDocListener: UploadDocListener) : DialogFragment() {
    private var _binding: FragmentImagePreviewBinding? = null
    private val binding get() = _binding!!

    private val uploadListener = uploadDocListener
    private var imagePath: String? = null
    private var imageuri: String? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imagePath = it.getString("path")
            imageuri = it.getString("uri")
            type = it.getString("type")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uri = imageuri?.toUri()
        val imageStream: InputStream = uri?.let {
            requireActivity().getContentResolver().openInputStream(
                it
            )
        }!!
        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
        binding.docPrevImageView.setImageBitmap(selectedImage)

        binding.backBtn.setOnClickListener {
            dismiss()
        }

        binding.docUploadBtn.setOnClickListener {
            imagePath?.let { path ->
                uploadListener?.uploadFile(path)
            }
            dismiss()
        }
    }


}