package com.example.caregiverphase2.ui.fragment

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.FragmentDocImagePreviewBinding
import com.example.caregiverphase2.utils.UploadDocumentListener
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class DocImagePreviewFragment(uploadDocumentListener: UploadDocumentListener) : DialogFragment() {
    private var _binding: FragmentDocImagePreviewBinding? = null
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
        _binding = FragmentDocImagePreviewBinding.inflate(inflater, container, false)
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


        //DOB from calender
        val cal = Calendar.getInstance()
        val c = cal.time
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "MM-dd-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                expiry_date = sdf.format(cal.time)
                binding.expiryDateTv.text = expiry_date
            }


        binding.expiryDateTv.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireActivity(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        binding.docUploadBtn.setOnClickListener {
            if(binding.expiryDateTv.text != "Select expiry date"){
                imagePath?.let { path ->
                    uploadListener?.uploadDoc(path, binding.expiryDateTv.text.toString())
                }
                dismiss()
            }else{
               Toast.makeText(requireActivity(),"Please select expiry date.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}