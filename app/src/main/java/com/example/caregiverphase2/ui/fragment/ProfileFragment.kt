package com.example.caregiverphase2.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.ProfileEducationAdapter
import com.example.caregiverphase2.adapter.ShowCertificateAdapter
import com.example.caregiverphase2.adapter.UpcommingJobsAdapter
import com.example.caregiverphase2.databinding.FragmentLoginBinding
import com.example.caregiverphase2.databinding.FragmentProfileBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_profile.Certificate
import com.example.caregiverphase2.model.pojo.get_profile.Education
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.AddBioActivity
import com.example.caregiverphase2.ui.activity.AddCertificateActivity
import com.example.caregiverphase2.ui.activity.AddEducationActivity
import com.example.caregiverphase2.ui.activity.ChooseLoginRegActivity
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.utils.UploadDocListener
import com.example.caregiverphase2.viewmodel.ChangeProfilePicViewModel
import com.example.caregiverphase2.viewmodel.GetProfileViewModel
import com.example.caregiverphase2.viewmodel.LogoutViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
class ProfileFragment : Fragment(), UploadDocListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE = 100

    private val mGetProfileViewModel: GetProfileViewModel by viewModels()
    private val mChangeProfilePicViewModel: ChangeProfilePicViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

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

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = requireActivity().loadingDialog()

        //observer
        getProfileObserve()
        changeProfilePicObserve()

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

        /*binding.userImgRelay.setOnClickListener {
            if (checkSelfPermission(requireActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(requireActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }*/

        binding.nameTv.text = PrefManager.getUserFullName()
    }

    override fun onResume() {
        if(requireActivity().isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
            loader.show()
        }else{
            Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_SHORT).show()
        }

        super.onResume()
    }

    private fun getProfileObserve(){
        mGetProfileViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val data = outcome.data?.data

                        data?.basic_info?.photo?.let {
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+it) // image url
                                .placeholder(R.color.dash_yellow) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.userImgView)
                        }
                        data?.basic_info?.care_completed?.let {
                            binding.careCompletedTv.text = it.toString()
                        }
                        data?.basic_info?.user?.email?.let {
                            binding.emailTv.text = it.toString()
                        }
                        data?.basic_info?.phone?.let {
                            binding.phoneTv.text = it.toString()
                        }
                        data?.basic_info?.experience?.let {
                            binding.expTv.text = it.toString()+" Years"
                        }
                        data?.basic_info?.gender?.let {
                            binding.genderTv.text = it.toString()
                        }
                        data?.basic_info?.bio?.let {
                            binding.showBioTv.text = it.toString()
                        }
                        data?.basic_info?.dob?.let {
                            binding.ageTv.text = it.toString()+" Years"
                        }
                        if(data?.basic_info?.bio != null){
                            binding.addBioBtn.gone()
                            binding.bioImg.gone()
                            binding.bioHtv.gone()
                            binding.showBioHtv.visible()
                            binding.showBioTv.visible()
                            binding.editBioBtn.visible()
                        }else{
                            binding.addBioBtn.visible()
                            binding.bioImg.visible()
                            binding.bioHtv.visible()
                            binding.showBioHtv.gone()
                            binding.showBioTv.gone()
                            binding.editBioBtn.gone()
                        }

                        if(data?.education != null && data.education.isNotEmpty()){
                            binding.educationRecycler.visible()
                            binding.showEducationHtv.visible()
                            binding.eduImg.gone()
                            binding.eduHtv.gone()
                            binding.addEduBtn.gone()
                            fillEducationRecycler(data?.education)
                        }else{
                            binding.educationRecycler.gone()
                            binding.showEducationHtv.gone()
                            binding.eduImg.visible()
                            binding.eduHtv.visible()
                            binding.addEduBtn.visible()
                        }

                        if(data?.certificate != null && data.certificate.isNotEmpty()){
                            binding.certificateRecycler.visible()
                            binding.showCertificateHtv.visible()
                            binding.addCertificateBtn.gone()
                            binding.certificateHtv.gone()
                            binding.certificateImg.gone()
                            fillCertificateRecycler(data?.certificate)
                        }else{
                            binding.certificateRecycler.gone()
                            binding.showCertificateHtv.gone()
                            binding.addCertificateBtn.visible()
                            binding.certificateHtv.visible()
                            binding.certificateImg.visible()
                        }

                        mGetProfileViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun fillEducationRecycler(list: List<Education>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.educationRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = ProfileEducationAdapter(list,requireActivity())
        }
    }

    private fun fillCertificateRecycler(list: List<Certificate>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.certificateRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = ShowCertificateAdapter(list,requireActivity())
        }
    }

    private fun changeProfilePicObserve(){
        mChangeProfilePicViewModel.response.observe(viewLifecycleOwner, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        outcome.data?.data?.let {
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+it) // image url
                                .placeholder(R.color.dash_yellow) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.userImgView)
                        }
                        mChangeProfilePicViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    override fun uploadFile(path: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    val file = File(absolutePath)
                    val compressedImageFile = Compressor.compress(requireActivity(), file)
                    val imagePart = requireActivity().createMultiPart("photo", compressedImageFile)
                    if(requireActivity().isConnectedToInternet()){
                        mChangeProfilePicViewModel.changeProfilePic(
                            imagePart,
                            accessToken
                        )
                        loader.show()
                    }else{
                        Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager())
            {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data =
                        Uri.parse(String.format("package:%s", requireActivity().applicationContext.packageName))
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
            cursor = requireActivity().contentResolver.query(contentUri!!, proj, null, null, null)
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
        dialogFragment.show(requireActivity().supportFragmentManager, "signature")
    }

    private fun requestStoragePermission() {
        Dexter.withActivity(requireActivity())
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
                            requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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
                    Toast.makeText(requireActivity(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}