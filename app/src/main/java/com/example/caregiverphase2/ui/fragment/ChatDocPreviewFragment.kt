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
import com.example.caregiverphase2.databinding.FragmentChatDocPreviewBinding
import com.example.caregiverphase2.databinding.FragmentDocImagePreviewBinding
import com.example.caregiverphase2.utils.UploadDocumentListener
import hideSoftKeyboard
import java.io.InputStream

class ChatDocPreviewFragment(uploadDocumentListener: UploadDocumentListener) : DialogFragment() {
    private var _binding: FragmentChatDocPreviewBinding? = null
    private val binding get() = _binding!!
    private val uploadListener = uploadDocumentListener
    private var imagePath: String? = null
    private var imageuri: String? = null
    private var expiry_date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imagePath = it.getString("path")
            imageuri = it.getString("uri")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatDocPreviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
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

        binding.chatBtnSend.setOnClickListener {
            val caption = binding.textInput.text.toString()
            imagePath?.let { path ->
                uploadListener?.uploadDoc(path, caption)
            }
            requireActivity().hideSoftKeyboard()
            dismiss()
        }
    }
}