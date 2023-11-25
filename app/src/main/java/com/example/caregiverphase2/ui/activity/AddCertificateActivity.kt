package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.PermissionChecker
import androidx.core.widget.doOnTextChanged
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAddCertificateBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.ImagePreviewFragment
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.utils.UploadDocListener
import com.example.caregiverphase2.viewmodel.AddCertificateViewModel
import com.example.caregiverphase2.viewmodel.RegisterViewModel
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import createMultiPart
import dagger.hilt.android.AndroidEntryPoint
import hideSoftKeyboard
import id.zelory.compressor.Compressor
import isConnectedToInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import loadingDialog
import showKeyboard
import java.io.File
import java.io.InputStream

@AndroidEntryPoint
class AddCertificateActivity : AppCompatActivity(), UploadDocListener {
    private lateinit var binding: ActivityAddCertificateBinding

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE = 100

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mAddCertificateViewModel: AddCertificateViewModel by viewModels()

    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.clearBtn.setOnClickListener {
            finish()
        }

        binding.imageFrame.setOnClickListener {
            if(PermissionChecker.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED){
                dispatchGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }

        binding.addBtn.setOnClickListener {
            val validCertificateName = binding.certificateNameTxtLay.helperText == null && binding.certificateNameTxt.text.toString().isNotEmpty()
            val validStartYear = binding.startYearTxtLay.helperText == null && binding.startYearTxt.text.toString().isNotEmpty()
            val validEndYear = binding.endYearTxtLay.helperText == null && binding.endYearTxt.text.toString().isNotEmpty()

            if(validCertificateName){
                if(validStartYear){
                    if(validEndYear){
                        if(binding.endYearTxt.text.toString().toInt() - binding.startYearTxt.text.toString().toInt() >= 0){
                            if(imageUri != null){
                                try {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        withContext(Dispatchers.Main) {
                                            val file = File(absolutePath)
                                            val compressedImageFile = Compressor.compress(this@AddCertificateActivity, file)
                                            val imagePart = createMultiPart("document", compressedImageFile)
                                            if(isConnectedToInternet()){
                                                mAddCertificateViewModel.addCertificate(
                                                    imagePart,
                                                    binding.certificateNameTxt.text.toString(),
                                                    binding.startYearTxt.text.toString(),
                                                    binding.endYearTxt.text.toString(),
                                                    accessToken
                                                )
                                                hideSoftKeyboard()
                                                loader.show()
                                            }else{
                                                Toast.makeText(this@AddCertificateActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                                            }

                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }else{
                                Toast.makeText(this,"upload a certificate image.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"please provide a valid start year and end year",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        if(binding.endYearTxtLay.helperText == null) binding.endYearTxtLay.helperText = "required"
                        binding.endYearTxt.showKeyboard()
                    }
                }else{
                    if(binding.startYearTxtLay.helperText == null) binding.startYearTxtLay.helperText = "required"
                    binding.startYearTxt.showKeyboard()
                }
            }else{
                if(binding.certificateNameTxtLay.helperText == null) binding.certificateNameTxtLay.helperText = "required"
                binding.certificateNameTxt.showKeyboard()
            }

        }

        //validation
        certificateNameFocusListener()
        startYearFocusListener()
        endYearFocusListener()

        //observer
        addCertificateObserve()
    }

    private fun certificateNameFocusListener(){
        binding.certificateNameTxt.doOnTextChanged { text, start, before, count ->
            binding.certificateNameTxtLay.helperText = validCertificateName()
        }
    }

    private fun validCertificateName(): String? {
        val nameText = binding.certificateNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide school name."
        }
        return null
    }

    private fun startYearFocusListener(){
        binding.startYearTxt.doOnTextChanged { text, start, before, count ->
            binding.startYearTxtLay.helperText = validStartYear()
        }
    }

    private fun validStartYear(): String? {
        val nameText = binding.startYearTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide start year."
        }
        return null
    }

    private fun endYearFocusListener(){
        binding.endYearTxt.doOnTextChanged { text, start, before, count ->
            binding.endYearTxtLay.helperText = validEndYear()
        }
    }

    private fun validEndYear(): String? {
        val nameText = binding.endYearTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide end year."
        }
        return null
    }

    override fun uploadFile(path: String) {
        val uri = imageUri
        val imageStream: InputStream = uri?.let {
            this.contentResolver.openInputStream(
                it
            )
        }!!
        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
        binding.certificateImg.setImageBitmap(selectedImage)
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager())
            {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data =
                        Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivityForResult(intent, 2296)
                } catch (e: java.lang.Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityForResult(intent, 2296)
                }
            }else{
                dispatchGalleryIntent()
            }
        } else {
            requestStoragePermission()
        }
    }

    private fun dispatchGalleryIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.setType("image/*")
        startActivityForResult(gallery, PICK_IMAGE)
    }

    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = this.contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun showImageDialog(absolutePath: String,uri: String,type: String) {
        val bundle = Bundle()
        bundle.putString("path", absolutePath)
        bundle.putString("uri",uri)
        bundle.putString("type",type)
        val dialogFragment = ImagePreviewFragment(this)
        dialogFragment.arguments = bundle
        dialogFragment.show(this.supportFragmentManager, "signature")
    }

    private fun requestStoragePermission() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    dispatchGalleryIntent()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    requestStoragePermission()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }


            })
            .onSameThread()
            .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                imageUri = data?.data
                val path = getRealPathFromUri(imageUri)
                val imageFile = File(path!!)
                absolutePath = imageFile.absolutePath
                showImageDialog(imageFile.absolutePath,imageUri.toString(),"covid")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    dispatchGalleryIntent()
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private fun addCertificateObserve(){
        mAddCertificateViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mAddCertificateViewModel.navigationComplete()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}