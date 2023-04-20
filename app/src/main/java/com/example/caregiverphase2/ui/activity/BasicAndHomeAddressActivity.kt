package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.*
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.model.pojo.get_documents.*
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.DocImagePreviewFragment
import com.example.caregiverphase2.ui.fragment.ImagePreviewFragment
import com.example.caregiverphase2.utils.*
import com.example.caregiverphase2.viewmodel.*
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetDialog
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


    var job_address: String = ""
    var place_name: String = ""
    var lat: String = ""
    var lang: String = ""

    var street_n = ""
    var city_n = ""
    var state_n = ""
    var zipcode_n = ""
    var building_n = ""
    var floor_n = ""

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
                binding.addressCard.gone()
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

        autocomplete()

        binding.dobTv.gone()

        loader = this.loadingDialog()
        Places.initialize(applicationContext, getString(R.string.api_key))

        //spinner
        setUpGenderSpinner()
        setUpJobTypeSpinner()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.docStep2.setOnClickListener {
            binding.relativeLay1.gone()
            binding.relativeLay2.visible()
            binding.relativeLay3.gone()
            binding.skipBtn.gone()

            binding.optionalDot2.background = ContextCompat.getDrawable(this, R.drawable.reg_dot_green)
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

            mGetRegistrationDetailsViewModel.getRegDetails(accessToken)
            loader.show()
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

            val currentYear: Int = c.get(Calendar.YEAR)
            val currentMonth: Int = c.get(Calendar.MONTH)
            val currentDay: Int = c.get(Calendar.DAY_OF_MONTH)

            val minYear = currentYear - 18

            c.set(minYear, currentMonth, currentDay)
            val minDateInMilliSeconds: Long = c.getTimeInMillis()
            dpd.getDatePicker().setMaxDate(minDateInMilliSeconds)


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
                                if(binding.fullAddressTv.text.toString().isNotEmpty()){
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
                                                        binding.fullAddressTv.text.toString(),
                                                        short_address,
                                                        street_n,
                                                        city_n,
                                                        state_n,
                                                        zipcode_n,
                                                        building_n,
                                                        floor_n,
                                                        "USA",
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

        binding.addImageBtn.setOnClickListener {
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchGalleryIntent()
            }else{
                requestStoragePermission(false)
            }
        }

        binding.skipBtn.setOnClickListener {
            if(isConnectedToInternet()){

                mSubmitOptionalRegViewModel.submitOptionalReg(job_type = job_type, experience = binding.experienceTxt.text.toString(),token = accessToken)
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

        return null
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


    private fun autocomplete(){
        val autocompleteFragment = supportFragmentManager?.findFragmentById(R.id.basic_autocomplete_fragment) as AutocompleteSupportFragment

        val etTextInput: EditText = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        etTextInput.setTextColor(R.color.black)
        etTextInput.setTextSize(14.5f)
        etTextInput.setHint(R.string.search_loc)
        etTextInput.setHintTextColor(R.color.black)

        /*val ivSearch: ImageView = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_button)
        ivSearch.setImageResource(R.drawable.ic_gps_19)*/

        //autocompleteFragment?.setTypeFilter(TypeFilter.ESTABLISHMENT)
        autocompleteFragment?.setCountries("US")
        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS))

        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                job_address = place.address
                place_name = place.name

                val latLangList = place.latLng.toString().split("(").toTypedArray()
                val final_latLangList = latLangList[1].toString().split(",").toTypedArray()
                lat = final_latLangList[0].toString()
                lang = final_latLangList[1].toString().substring(0, final_latLangList[1].length - 1)

                var streetName = ""
                var streetNumber = ""
                var city = ""
                var state = ""
                var zipcode = ""

                for (i in place.addressComponents.asList()){
                    if(i.types[0] == "locality"){
                        city = i.name
                    }
                    if(i.types[0] == "route"){
                        streetName = i.name.toString()
                    }
                    if(i.types[0] == "street_number"){
                        streetNumber = i.name.toString()
                    }
                    if(i.types[0] == "administrative_area_level_1"){
                        state = i.name.toString()
                    }
                    if(i.types[0] == "postal_code"){
                        zipcode = i.name.toString()
                    }
                }

                showAddressBottomSheet(place_name, streetName, streetNumber, city, state, zipcode)

            }

            override fun onError(status: Status) {
                Log.i("place2", "An error occurred: $status")
            }
        })
    }

    private fun showAddressBottomSheet(
        subLocality: String,
        streetName: String = "",
        streetNumber: String = "",
        city: String? = null,
        state: String? = null,
        zipcode: String? = null,
        building: String? = null,
        floor: String? = null
    ){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.address_fill_bottomsheet_layout, null)

        val btnSave = view.findViewById<CardView>(R.id.save_address_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)
        val streetTxt = view.findViewById<EditText>(R.id.street_txt)
        val cityTxt = view.findViewById<EditText>(R.id.city_txt)
        val stateTxt = view.findViewById<EditText>(R.id.state_txt)
        val zipcodeTxt = view.findViewById<EditText>(R.id.zipcode_txt)
        val buildingTxt = view.findViewById<EditText>(R.id.building_txt)
        val floorTxt = view.findViewById<EditText>(R.id.floor_txt)

        var streetVar = ""
        if(streetName.isEmpty() && streetNumber.isEmpty()){
            streetVar = " "
        }else if(streetName.isEmpty() && streetNumber.isNotEmpty()){
            streetVar = streetNumber
        }else if(streetName.isNotEmpty() && streetNumber.isEmpty()){
            streetVar = streetName
        }else if(streetName.isNotEmpty() && streetNumber.isNotEmpty()){
            streetVar = streetNumber+", "+streetName
        }

        streetTxt.text = Editable.Factory.getInstance().newEditable(streetVar)

        city?.let{
            cityTxt.text = Editable.Factory.getInstance().newEditable(city)
        }
        state?.let {
            stateTxt.text = Editable.Factory.getInstance().newEditable(state)
        }
        zipcode?.let {
            zipcodeTxt.text = Editable.Factory.getInstance().newEditable(zipcode)
        }
        building?.let {
            buildingTxt.text = Editable.Factory.getInstance().newEditable(building)
        }
        floor?.let {
            floorTxt.text = Editable.Factory.getInstance().newEditable(floor)
        }

        btnClear.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            street_n = streetTxt.text.toString()
            city_n = cityTxt.text.toString()
            state_n = stateTxt.text.toString()
            zipcode_n = zipcodeTxt.text.toString()
            building_n = buildingTxt.text.toString()
            floor_n = floorTxt.text.toString()
            if(!street_n.isEmpty()){
                if(!city_n.isEmpty()){
                    if(!state_n.isEmpty()){
                        if(!zipcode_n.isEmpty()){
                            if(zipcode_n.length >= 5){
                                binding.addressCard.visible()

                                binding.fullAddressTv.text = subLocality+", "+street_n+", "+city_n+", "+state_n+", "+zipcode
                                binding.cityNameTv.text = city_n
                                short_address = city_n
                                binding.streetTv.text = street_n
                                binding.buildingTv.text = building_n

                                if(!floor_n.isEmpty()){
                                    binding.buildingTv.text = building_n+", "+floor_n
                                }
                                dialog.dismiss()
                            }else{
                                Toast.makeText(this,"provide a valid zipcode", Toast.LENGTH_SHORT).show()
                                zipcodeTxt.showKeyboard()
                            }
                        }else{
                            Toast.makeText(this,"provide zipcode", Toast.LENGTH_SHORT).show()
                            zipcodeTxt.showKeyboard()
                        }
                    }else{
                        Toast.makeText(this,"provide state name", Toast.LENGTH_SHORT).show()
                        stateTxt.showKeyboard()
                    }
                }else{
                    Toast.makeText(this,"provide city name", Toast.LENGTH_SHORT).show()
                    cityTxt.showKeyboard()
                }
            }else{
                Toast.makeText(this,"provide street name", Toast.LENGTH_SHORT).show()
                streetTxt.showKeyboard()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
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

    private fun requestStoragePermission(isDoc: Boolean) {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    if(isDoc){
                        dispatchDocGalleryIntent()
                    }else{
                        dispatchGalleryIntent()
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    requestStoragePermission(isDoc)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
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

        if (requestCode == 2297) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    dispatchDocGalleryIntent()
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
                        val intent = Intent(this, DocumentSubmitSuccessActivity::class.java)
                        startActivity(intent)
                        finish()
                        mUpdateDocumentStatusViewModel.navigationComplete()
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
                            binding.addressCard.visible()
                            binding.fullAddressTv.text = outcome.data?.data!!.full_address.toString()
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
                            dob = binding.dobTv.text.toString()
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
                        /*outcome.data?.data!!.photo?.let {
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+outcome.data?.data!!.photo) // image url
                                .placeholder(R.color.dash_yellow) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.userImg)
                        }*/
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
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
            }
        }
        binding.covidBtn.setOnClickListener {
            doc_type = "covid"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
            }
        }
        binding.criminalBtn.setOnClickListener {
            doc_type = "criminal"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
            }
        }
        binding.childAbuseBtn.setOnClickListener {
            doc_type = "childAbuse"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
            }
        }
        binding.w4Btn.setOnClickListener {
            doc_type = "w4_form"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
            }
        }
        binding.employmentBtn.setOnClickListener {
            doc_type = "employment"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
            }
        }
        binding.drivingBtn.setOnClickListener {
            doc_type = "driving"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
            }
        }
        binding.identityBtn.setOnClickListener {
            doc_type = "identification"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission(true)
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
        }
    }

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