package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.databinding.ActivityJobDetailsBinding
import com.example.caregiverphase2.ui.fragment.ImagePreviewFragment
import com.example.caregiverphase2.utils.UploadDocListener
import com.example.caregiverphase2.viewmodel.RegisterViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import createMultiPart
import dagger.hilt.android.AndroidEntryPoint
import gone
import hideSoftKeyboard
import id.zelory.compressor.Compressor
import isConnectedToInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import loadingDialog
import visible
import java.io.File
import java.io.InputStream
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

@AndroidEntryPoint
class BasicAndHomeAddressActivity : AppCompatActivity(), UploadDocListener {
    private lateinit var binding: ActivityBasicAndHomeAddressBinding
    val genderList: Array<String> =  arrayOf("Select gender", "Male", "Female", "Other")

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE = 100

    private val mRegisterViewModel: RegisterViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String
    private var gender: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBasicAndHomeAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //stepper
        //binding.relativeLay1.gone()
        binding.relativeLay2.gone()
        binding.relativeLay3.gone()

        binding.dobTv.gone()

        loader = this.loadingDialog()

        //spinner
        setUpGenderSpinner()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.dobAddBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                binding.dobTv.visible()
                binding.dobTv.setText("" + dayOfMonth + "-" + monthOfYear+1 + "-" + year)
            }, year, month, day)
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
            dpd.show()
        }

        binding.nextCardBtn.setOnClickListener {

            /*try {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        val file = File(absolutePath)
                        val compressedImageFile = Compressor.compress(this@BasicAndHomeAddressActivity, file)
                        val imagePart = createMultiPart("photo", compressedImageFile)
                        if(isConnectedToInternet()){
                            mRegisterViewModel.register(
                                imagePart,
                                binding.mobileNumberTxt.text.toString(),
                                "21/01.1998",
                                "male",
                                binding.ssnNumberTxt.text.toString(),
                                "test",
                                "part time",
                                "wall street",
                                "boston",
                                "AL",
                                "test",
                                token = accessToken
                            )
                            hideSoftKeyboard()
                            //loader.show()
                        }else{
                            Toast.makeText(this@BasicAndHomeAddressActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }*/


            binding.relativeLay1.gone()
            binding.relativeLay3.gone()
            binding.relativeLay2.visible()
        }

        binding.nextCardBtn2.setOnClickListener {
            binding.relativeLay2.gone()
            binding.relativeLay1.gone()
            binding.relativeLay3.visible()
        }

        binding.nextCardBtn3.setOnClickListener {
            finish()
        }

        binding.imageAddBtn.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
    }

    private fun setUpGenderSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,genderList)
        binding.genderSpinner.adapter = arrayAdapter
        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    gender = ""
                }else{
                    gender = genderList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

    override fun uploadFile(path: String) {
        val uri = imageUri
        val imageStream: InputStream = uri?.let {
            this.contentResolver.openInputStream(
                it
            )
        }!!
        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
        binding.userImg.setImageBitmap(selectedImage)
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
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {

                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // info("onPermissionsChecked: All permissions are granted!")
                        val telephonyManager =
                            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        mImeiId =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                try {
                                    telephonyManager.imei
                                } catch (e: SecurityException) {
                                    e.printStackTrace()
                                    "mxmxmxmxmxmxmxm"
                                }
                            } else {
                                "mxmxmxmxmxmxmxm"
                            }

                        grantedOtherPermissions = true
                    }

                    // check for permanent denial of any permission
                    /* if (report.isAnyPermissionPermanentlyDenied) {
                         // show alert dialog navigating to Settings
                         showSettingsDialog()
                     }*/
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
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
}