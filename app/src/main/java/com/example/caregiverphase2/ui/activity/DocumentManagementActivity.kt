package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.*
import com.example.caregiverphase2.databinding.ActivityChooseLoginRegBinding
import com.example.caregiverphase2.databinding.ActivityDocumentManagementBinding
import com.example.caregiverphase2.model.pojo.get_documents.*
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.DocImagePreviewFragment
import com.example.caregiverphase2.utils.DeleteDocClickListener
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.utils.UploadDocListener
import com.example.caregiverphase2.utils.UploadDocumentListener
import com.example.caregiverphase2.viewmodel.DeleteDocumentsViewModel
import com.example.caregiverphase2.viewmodel.GetDocumentsViewModel
import com.example.caregiverphase2.viewmodel.UpdateDocumentStatusViewModel
import com.example.caregiverphase2.viewmodel.UploadDocumentsViewModel
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
import gone
import id.zelory.compressor.Compressor
import isConnectedToInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import loadingDialog
import visible
import java.io.File

@AndroidEntryPoint
class DocumentManagementActivity : AppCompatActivity(), UploadDocListener, UploadDocumentListener,
    DeleteDocClickListener {
    private lateinit var binding: ActivityDocumentManagementBinding

    private val mGetDocumentsViewModel: GetDocumentsViewModel by viewModels()
    private val mDeleteDocumentsViewModel: DeleteDocumentsViewModel by viewModels()
    private val mUpdateDocumentStatusViewModel: UpdateDocumentStatusViewModel by viewModels()
    private val mUploadDocumentsViewModel: UploadDocumentsViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    private lateinit var doc_type: String

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE_DOC = 101

    private lateinit var accessToken: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDocumentManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.submitDocument.gone()

        binding.backArrow.setOnClickListener {
            finish()
        }

        docUpload()

        //observer
        getDocumentObserve()
        uploadDocumentObserve()
        deleteDocumentObserve()
        updateDocumentStatusObserve()
    }

    override fun onResume() {
        super.onResume()

        binding.submitDocument.gone()

        if(isConnectedToInternet()){
            binding.docsListMainLayout.gone()
            binding.docsShimmerView.visible()
            binding.docsShimmerView.startShimmer()
            mGetDocumentsViewModel.getDocuments(accessToken)
        }else{
            Toast.makeText(this,"Oops!! No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestStoragePermission() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    dispatchDocGalleryIntent()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    requestStoragePermission()
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

    private fun docUpload(){
        binding.submitDocument.setOnClickListener {
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
                requestStoragePermission()
            }
        }
        binding.covidBtn.setOnClickListener {
            doc_type = "covid"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }
        binding.criminalBtn.setOnClickListener {
            doc_type = "criminal"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }
        binding.childAbuseBtn.setOnClickListener {
            doc_type = "childAbuse"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }
        binding.employmentBtn.setOnClickListener {
            doc_type = "employment"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }
        binding.drivingBtn.setOnClickListener {
            doc_type = "driving"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }
        binding.identityBtn.setOnClickListener {
            doc_type = "identification"
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchDocGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }
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

    private fun showDocImageDialog(absolutePath: String,uri: String) {
        val bundle = Bundle()
        bundle.putString("path", absolutePath)
        bundle.putString("uri",uri)
        val dialogFragment = DocImagePreviewFragment(this)
        dialogFragment.arguments = bundle
        dialogFragment.show(this.supportFragmentManager, "signature")
    }

    private fun fillTuberculosisRecycler(list: MutableList<Tuberculosi>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.tuberRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = TuberculosisListAdapter(list,this@DocumentManagementActivity, this@DocumentManagementActivity)
        }
    }
    private fun fillCovidRecycler(list: MutableList<Covid>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.covidBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = CovidListAdapter(list,this@DocumentManagementActivity, this@DocumentManagementActivity)
        }
    }
    private fun fillCriminalRecycler(list: MutableList<Criminal>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.criminalBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = CriminalListAdapter(list,this@DocumentManagementActivity, this@DocumentManagementActivity)
        }
    }
    private fun fillChildAbuseRecycler(list: MutableList<ChildAbuse>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.childAbuseBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = ChildAbuseListAdapter(list,this@DocumentManagementActivity, this@DocumentManagementActivity)
        }
    }
    private fun fillEmploymentRecycler(list: MutableList<Employment>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.employmentBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = EmploymentListAdapter(list,this@DocumentManagementActivity, this@DocumentManagementActivity)
        }
    }
    private fun fillDrivingRecycler(list: MutableList<Driving>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.drivingBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = DrivingLiscenceListAdapter(list,this@DocumentManagementActivity, this@DocumentManagementActivity)
        }
    }
    private fun fillIdentityRecycler(list: MutableList<Identification>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.identityBgRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = IdentityListAdapter(list,this@DocumentManagementActivity, this@DocumentManagementActivity)
        }
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
                        outcome.data?.data!!.employment?.let {
                            fillEmploymentRecycler(outcome.data?.data!!.employment.toMutableList())
                        }
                        outcome.data?.data!!.driving?.let {
                            fillDrivingRecycler(outcome.data?.data!!.driving.toMutableList())
                        }
                        outcome.data?.data!!.identification?.let {
                            fillIdentityRecycler(outcome.data?.data!!.identification.toMutableList())
                        }
                        outcome.data?.data!!.caregiver_profile_status?.let {
                            if(it.is_documents_uploaded == 1){
                                binding.submitDocument.gone()
                            }else{
                                binding.submitDocument.visible()
                            }
                        }

                        /*val data = outcome.data?.data!!
                        if(!data.tuberculosis.isEmpty() && !data.covid.isEmpty() && !data.criminal.isEmpty() &&
                                !data.child_abuse.isEmpty() && !data.w4_form.isEmpty() && !data.employment.isEmpty() &&
                                !data.driving.isEmpty() && !data.identification.isEmpty()){
                            binding.submitDocument.visible()
                        }else{
                            binding.submitDocument.gone()
                        }*/

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

    override fun deleteDoc(id: Int, category: String) {
        deleteAlertDialog(id, category)
    }

    override fun uploadFile(path: String) {

    }

    override fun uploadDoc(path: String, expiry: String?) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    val file = File(path)
                    val compressedImageFile = Compressor.compress(this@DocumentManagementActivity, file)
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
                        Toast.makeText(this@DocumentManagementActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
                    dispatchDocGalleryIntent()
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}