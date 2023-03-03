package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.*
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.model.pojo.get_documents.*
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.DocImagePreviewFragment
import com.example.caregiverphase2.ui.fragment.ImagePreviewFragment
import com.example.caregiverphase2.utils.*
import com.example.caregiverphase2.viewmodel.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
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
import showKeyboard
import visible
import java.io.File
import java.io.InputStream
import java.util.*

@AndroidEntryPoint
class BasicAndHomeAddressActivity : AppCompatActivity(), UploadDocListener, UploadDocumentListener, DeleteDocClickListener{
    private lateinit var binding: ActivityBasicAndHomeAddressBinding
    val genderList: Array<String> =  arrayOf("Select gender", "Male", "Female", "Other")
    val jobTypeList: Array<String> =  arrayOf("Select job type", "Full time", "Part time")

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE = 100
    private val PICK_IMAGE_DOC = 101

    private val mRegisterViewModel: RegisterViewModel by viewModels()
    private val mUploadDocumentsViewModel: UploadDocumentsViewModel by viewModels()
    private val mSubmitOptionalRegViewModel: SubmitOptionalRegViewModel by viewModels()
    private val mGetDocumentsViewModel: GetDocumentsViewModel by viewModels()
    private val mDeleteDocumentsViewModel: DeleteDocumentsViewModel by viewModels()
    private val mUpdateDocumentStatusViewModel: UpdateDocumentStatusViewModel by viewModels()
    private val mGetRegistrationDetailsViewModel: GetRegistrationDetailsViewModel by viewModels()

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String
    private var gender: String = ""
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var full_address: String = ""
    private var short_address: String = ""
    private var dob: String = ""
    private var job_type: String = ""
    private lateinit var doc_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBasicAndHomeAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            val step = intent?.getStringExtra("step")!!

            if(step == "1"){
                binding.relativeLay1.visible()
                binding.relativeLay2.gone()
                binding.relativeLay3.gone()
                binding.skipBtn.gone()
            }else if(step == "2"){
                binding.relativeLay1.gone()
                binding.relativeLay2.visible()
                binding.relativeLay3.gone()
                binding.skipBtn.visible()
            }else if(step == "3"){
                binding.relativeLay1.gone()
                binding.relativeLay2.gone()
                binding.relativeLay3.visible()
                binding.skipBtn.gone()
            }
        }
        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.dobTv.gone()

        loader = this.loadingDialog()
        Places.initialize(applicationContext, getString(R.string.api_key))

        //spinner
        setUpGenderSpinner()
        setUpJobTypeSpinner()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.basicStep2.setOnClickListener {
            /*binding.relativeLay1.gone()
            binding.relativeLay2.visible()
            binding.relativeLay3.gone()
            binding.skipBtn.visible()*/
        }

        binding.basicStep3.setOnClickListener {
            /*binding.relativeLay1.gone()
            binding.relativeLay2.gone()
            binding.relativeLay3.visible()
            binding.skipBtn.gone()*/
        }

        binding.docStep2.setOnClickListener {
            binding.relativeLay1.gone()
            binding.relativeLay2.visible()
            binding.relativeLay3.gone()
            binding.skipBtn.visible()

            binding.optionalDot1.background = ContextCompat.getDrawable(this, R.drawable.reg_dot_green)
            binding.optionalStep2.background = ContextCompat.getDrawable(this, R.drawable.stepper_done_icon)
            mGetRegistrationDetailsViewModel.getRegDetails(accessToken)
            loader.show()
        }

        binding.docStep1.setOnClickListener {
            binding.relativeLay1.visible()
            binding.relativeLay2.gone()
            binding.relativeLay3.gone()
            binding.skipBtn.gone()

            binding.basicDot1.background = ContextCompat.getDrawable(this, R.drawable.reg_dot_green)
            binding.basicDot2.background = ContextCompat.getDrawable(this, R.drawable.reg_dot_green)
            binding.basicStep2.background = ContextCompat.getDrawable(this, R.drawable.stepper_done_icon)
            binding.basicStep1.background = ContextCompat.getDrawable(this, R.drawable.stepper_done_icon)

            mGetRegistrationDetailsViewModel.getRegDetails(accessToken)
            loader.show()
        }

        binding.optionalStep1.setOnClickListener {
            binding.relativeLay1.visible()
            binding.relativeLay2.gone()
            binding.relativeLay3.gone()
            binding.skipBtn.gone()

            binding.basicDot1.background = ContextCompat.getDrawable(this, R.drawable.reg_dot_green)
            binding.basicStep1.background = ContextCompat.getDrawable(this, R.drawable.stepper_done_icon)
        }

        binding.optionalStep3.setOnClickListener {
            /*binding.relativeLay1.gone()
            binding.relativeLay2.gone()
            binding.relativeLay3.visible()
            binding.skipBtn.gone()*/
        }

        binding.dobAddBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                binding.dobTv.visible()

                var day = ""
                var month = ""
                if(dayOfMonth<10){
                    day = "0"+dayOfMonth.toString()
                }else{
                    day = dayOfMonth.toString()
                }
                if(monthOfYear+1 < 10){
                    month = "0"+(monthOfYear+1)
                }else{
                    month = (monthOfYear+1).toString()
                }
                binding.dobTv.setText("" +month + "-" + day+ "-" + year)
                dob = binding.dobTv.text.toString()
            }, year, month, day)
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            dpd.show()
        }

        binding.nextCardBtn.setOnClickListener {
            val validMobile = binding.mobileNumberTxtLay.helperText == null && binding.mobileNumberTxt.text.toString().isNotEmpty()
            val validSsn = binding.ssnNumberTxtLay.helperText == null && binding.ssnNumberTxt.text.toString().isNotEmpty()

            if(imageUri != null){
                if(validMobile){
                    if(!dob.isEmpty()){
                        if(!gender.isEmpty()){
                            if(validSsn){
                                if(full_address.isNotEmpty()){
                                    try {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            withContext(Dispatchers.Main) {
                                                val file = File(absolutePath)
                                                val compressedImageFile = Compressor.compress(this@BasicAndHomeAddressActivity, file)
                                                val imagePart = createMultiPart("photo", compressedImageFile)
                                                if(isConnectedToInternet()){
                                                    mRegisterViewModel.register(
                                                        imagePart,
                                                        binding.mobileNumberTxt.text.toString(),
                                                        binding.dobTv.text.toString(),
                                                        gender,
                                                        binding.ssnNumberTxt.text.toString(),
                                                        full_address,
                                                        short_address,
                                                        accessToken
                                                    )
                                                    hideSoftKeyboard()
                                                    loader.show()
                                                }else{
                                                    Toast.makeText(this@BasicAndHomeAddressActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                                                }

                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }else{
                                    Toast.makeText(this,"Please add location/address.",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                if(binding.ssnNumberTxtLay.helperText == null) binding.ssnNumberTxtLay.helperText = "required"
                                binding.ssnNumberTxt.showKeyboard()
                            }
                        }else{
                            Toast.makeText(this,"Please select gender.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"Please select date of birth.",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if(binding.mobileNumberTxtLay.helperText == null) binding.mobileNumberTxtLay.helperText = "required"
                    binding.mobileNumberTxt.showKeyboard()
                }
            }else{
                Toast.makeText(this,"Please select a profile picture.",Toast.LENGTH_SHORT).show()
            }
        }

        binding.nextCardBtn2.setOnClickListener {

            if(isConnectedToInternet()){
                mSubmitOptionalRegViewModel.submitOptionalReg(job_type = job_type, experience = binding.experienceTxt.text.toString(),token = accessToken)
                loader.show()
            }else{
                Toast.makeText(this,"Oops!! No internet connection.",Toast.LENGTH_SHORT).show()
            }
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

        binding.addLocBtn.setOnClickListener {
            autocompleteWithIntent()
        }

        binding.skipBtn.setOnClickListener {
            if(isConnectedToInternet()){
                mSubmitOptionalRegViewModel.submitOptionalReg(job_type = job_type, experience = "",token = accessToken)
                loader.show()
            }else{
                Toast.makeText(this,"Oops!! No internet connection.",Toast.LENGTH_SHORT).show()
            }
        }

        //document upload
        docUpload()

        //observer
        addBasicInfoObserve()
        submitOptionalRegObserve()
        uploadDocumentObserve()
        getDocumentObserve()
        deleteDocumentObserve()
        updateDocumentStatusObserve()
        getRegistrationDetailsObserve()

        //validation
        mobileFocusListener()
        ssnFocusListener()

        binding.textView2.text = PrefManager.getUserFullName()
    }

    override fun onResume() {
        super.onResume()
        if(isConnectedToInternet()){
            binding.docsListMainLayout.gone()
            binding.docsShimmerView.visible()
            binding.docsShimmerView.startShimmer()
            mGetDocumentsViewModel.getDocuments(accessToken)
        }else{
            Toast.makeText(this,"Oops!! No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun mobileFocusListener(){
        binding.mobileNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.mobileNumberTxtLay.helperText = validMobileNumber()
        }
    }

    private fun validMobileNumber(): String? {
        val mobileText = binding.mobileNumberTxt.text.toString()
        if(mobileText.length != 10){
            return "10 digit number required."
        }

        if(binding.mobileNumberTxt.text.toString().toDouble() == 0.00){
            return "Please provide a valid phone number."
        }

        return  null
    }

    private fun ssnFocusListener(){
        binding.ssnNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.ssnNumberTxtLay.helperText = validSsnNumber()
        }
    }

    private fun validSsnNumber(): String? {
        val mobileText = binding.ssnNumberTxt.text.toString()
        if(mobileText.length != 9){
            return "9 digit number required."
        }

        if(binding.ssnNumberTxt.text.toString().toDouble() == 0.00){
            return "Please provide a valid tax number."
        }

        return  null
    }

    private fun setUpGenderSpinner(value: String? = null){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,genderList)
        binding.genderSpinner.adapter = arrayAdapter

        if (value != null) {
            val spinnerPosition: Int = arrayAdapter.getPosition(value)
            binding.genderSpinner.setSelection(spinnerPosition)
        }

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

    private fun setUpJobTypeSpinner(value: String? = null){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,jobTypeList)
        binding.jobTypeSpinner.adapter = arrayAdapter

        if (value != null) {
            val spinnerPosition: Int = arrayAdapter.getPosition(value)
            binding.jobTypeSpinner.setSelection(spinnerPosition)
        }

        binding.jobTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if(p2 == 0){
                    job_type = ""
                }else{
                    job_type = jobTypeList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

    private fun autocompleteWithIntent(){
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setLocationBias(
                RectangularBounds.newInstance(
                    LatLng(26.1442464,91.784392),
                    LatLng(26.1442464,91.784392)
                )
            )
            .setCountry("IN")
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
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

    private fun dispatchDocGalleryIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE_DOC)
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

    private fun showDocImageDialog(absolutePath: String,uri: String) {
        val bundle = Bundle()
        bundle.putString("path", absolutePath)
        bundle.putString("uri",uri)
        val dialogFragment = DocImagePreviewFragment(this)
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

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == PICK_IMAGE_DOC) {
            try {
                imageUri = data?.data
                val path = getRealPathFromUri(imageUri)
                val imageFile = File(path!!)
                absolutePath = imageFile.absolutePath
                showDocImageDialog(imageFile.absolutePath,imageUri.toString())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("place", "Place: ${place.name}, ${place.id}, ${place.latLng}")
                        binding.locTv.text = place.address
                        full_address = place.address
                        short_address = place.name
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("place", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
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

    private fun addBasicInfoObserve(){
        mRegisterViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        binding.relativeLay1.gone()
                        binding.relativeLay3.gone()
                        binding.relativeLay2.visible()
                        mRegisterViewModel.navigationComplete()
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

    private fun submitOptionalRegObserve(){
        mSubmitOptionalRegViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        //Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        binding.relativeLay2.gone()
                        binding.relativeLay1.gone()
                        binding.relativeLay3.visible()
                        binding.skipBtn.gone()

                        mRegisterViewModel.navigationComplete()
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

    private fun uploadDocumentObserve(){
        mUploadDocumentsViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()

                        binding.docsListMainLayout.gone()
                        binding.docsShimmerView.visible()
                        binding.docsShimmerView.startShimmer()
                        mGetDocumentsViewModel.getDocuments(accessToken)

                        mUploadDocumentsViewModel.navigationComplete()
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

    private fun getDocumentObserve(){
        mGetDocumentsViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.docsListMainLayout.visible()
                    binding.docsShimmerView.gone()
                    binding.docsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        outcome.data?.data!!.tuberculosis?.let {
                            fillTuberculosisRecycler(outcome.data?.data!!.tuberculosis.toMutableList())
                        }
                        outcome.data?.data!!.covid?.let {
                            fillCovidRecycler(outcome.data?.data!!.covid.toMutableList())
                        }
                        outcome.data?.data!!.criminal?.let {
                            fillCriminalRecycler(outcome.data?.data!!.criminal.toMutableList())
                        }
                        outcome.data?.data!!.child_abuse?.let {
                            fillChildAbuseRecycler(outcome.data?.data!!.child_abuse.toMutableList())
                        }
                        outcome.data?.data!!.w4_form?.let {
                            fillW4Recycler(outcome.data?.data!!.w4_form.toMutableList())
                        }
                        outcome.data?.data!!.employment?.let {
                            fillEmploymentRecycler(outcome.data?.data!!.employment.toMutableList())
                        }
                        outcome.data?.data!!.driving?.let {
                            fillDrivingRecycler(outcome.data?.data!!.driving.toMutableList())
                        }
                        outcome.data?.data!!.identification?.let {
                            fillIdentityRecycler(outcome.data?.data!!.identification.toMutableList())
                        }

                        mGetDocumentsViewModel.navigationComplete()
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

    private fun deleteDocumentObserve(){
        mDeleteDocumentsViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){

                        binding.docsListMainLayout.gone()
                        binding.docsShimmerView.visible()
                        binding.docsShimmerView.startShimmer()
                        mGetDocumentsViewModel.getDocuments(accessToken)

                        mDeleteDocumentsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    loader.dismiss()
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun updateDocumentStatusObserve(){
        mUpdateDocumentStatusViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mUpdateDocumentStatusViewModel.navigationComplete()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    loader.dismiss()
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getRegistrationDetailsObserve(){
        mGetRegistrationDetailsViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        outcome.data?.data!!.short_address?.let {
                            short_address = outcome.data?.data!!.short_address
                        }
                        outcome.data?.data!!.full_address?.let {
                            binding.locTv.text = outcome.data?.data!!.full_address.toString()
                            full_address = outcome.data?.data!!.full_address.toString()
                        }
                        outcome.data?.data!!.gender?.let {
                            gender = outcome.data?.data!!.gender
                            setUpGenderSpinner(outcome.data?.data!!.gender.toString())
                        }
                        outcome.data?.data!!.ssn?.let {
                            binding.ssnNumberTxt.text = Editable.Factory.getInstance().newEditable(outcome.data?.data!!.ssn.toString())
                        }
                        outcome.data?.data!!.dob?.let {
                            binding.dobTv.visible()
                            binding.dobHtv.gone()
                            binding.dobTv.text = outcome.data?.data!!.dob.toString()
                        }
                        outcome.data?.data!!.phone?.let {
                            binding.mobileNumberTxt.text = Editable.Factory.getInstance().newEditable(outcome.data?.data!!.phone)
                        }
                        outcome.data?.data?.job_type?.let {
                            job_type = outcome.data?.data?.job_type.toString()
                            setUpJobTypeSpinner(outcome.data?.data?.job_type.toString())
                        }
                        outcome.data?.data!!.experience?.let {
                            binding.experienceTxt.text = Editable.Factory.getInstance().newEditable(outcome.data?.data!!.experience.toString())
                        }
                        outcome.data?.data!!.photo?.let {
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+outcome.data?.data!!.photo) // image url
                                .placeholder(R.color.dash_yellow) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.userImg)
                        }
                        mGetRegistrationDetailsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    loader.dismiss()
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun docUpload(){
        binding.nextCardBtn3.setOnClickListener {
            if(isConnectedToInternet()){
                mUpdateDocumentStatusViewModel.updateDocStatus(accessToken)
                loader.show()
            }else{
                Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tuberculosisBtn.setOnClickListener {
            doc_type = "tuberculosis"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
        binding.covidBtn.setOnClickListener {
            doc_type = "covid"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
        binding.criminalBtn.setOnClickListener {
            doc_type = "criminal"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
        binding.childAbuseBtn.setOnClickListener {
            doc_type = "childAbuse"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
        binding.w4Btn.setOnClickListener {
            doc_type = "w4_form"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
        binding.employmentBtn.setOnClickListener {
            doc_type = "employment"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
        binding.drivingBtn.setOnClickListener {
            doc_type = "driving"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
        binding.identityBtn.setOnClickListener {
            doc_type = "identification"
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchDocGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }
    }

    private fun fillTuberculosisRecycler(list: MutableList<Tuberculosi>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.tuberRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = TuberculosisListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }
    private fun fillCovidRecycler(list: MutableList<Covid>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.covidBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = CovidListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }
    private fun fillCriminalRecycler(list: MutableList<Criminal>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.criminalBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = CriminalListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }
    private fun fillChildAbuseRecycler(list: MutableList<ChildAbuse>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.childAbuseBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = ChildAbuseListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }
    private fun fillW4Recycler(list: MutableList<W4Form>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.w4BgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = W4ListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }
    private fun fillEmploymentRecycler(list: MutableList<Employment>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.employmentBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = EmploymentListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }
    private fun fillDrivingRecycler(list: MutableList<Driving>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.drivingBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = DrivingLiscenceListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }
    private fun fillIdentityRecycler(list: MutableList<Identification>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.identityBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = IdentityListAdapter(list,this@BasicAndHomeAddressActivity, this@BasicAndHomeAddressActivity)
        }
    }

    override fun uploadDoc(path: String, expiry: String?) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    val file = File(path)
                    val compressedImageFile = Compressor.compress(this@BasicAndHomeAddressActivity, file)
                    val imagePart = createMultiPart("document", compressedImageFile)
                    if(isConnectedToInternet()){
                        mUploadDocumentsViewModel.uploadDocuments(
                            imagePart,
                            doc_type,
                            expiry,
                            accessToken
                        )
                        loader.show()
                    }else{
                        Toast.makeText(this@BasicAndHomeAddressActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }    }

    override fun deleteDoc(id: Int, category: String) {
        deleteAlertDialog(id, category)
    }

    private fun deleteAlertDialog(id: Int, category: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete !!")
        builder.setMessage("Do you want to remove this document permanently?")
        builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            mDeleteDocumentsViewModel.deleteDocument(category,id,accessToken)
            loader.show()
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}