package com.example.caregiverphase2.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
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
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.ProfileEducationAdapter
import com.example.caregiverphase2.adapter.ShowCertificateAdapter
import com.example.caregiverphase2.databinding.FragmentProfileBinding
import com.example.caregiverphase2.model.pojo.get_profile.Certificate
import com.example.caregiverphase2.model.pojo.get_profile.Education
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.AddBioActivity
import com.example.caregiverphase2.ui.activity.AddCertificateActivity
import com.example.caregiverphase2.ui.activity.AddEducationActivity
import com.example.caregiverphase2.ui.activity.BasicAndHomeAddressActivity
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.utils.UploadDocListener
import com.example.caregiverphase2.viewmodel.ChangeProfilePicViewModel
import com.example.caregiverphase2.viewmodel.GetProfileViewModel
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
    private lateinit var accessToken: String
    private var isEdit: Boolean = false

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

        binding.changePicBtn.gone()

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

        binding.eduMenuBtn.setOnClickListener {
            showEduPopup(it)
        }

        binding.certificateMenuBtn.setOnClickListener {
            showCertificatePopup(it)
        }

        binding.changePicBtn.setOnClickListener {
            if (checkSelfPermission(requireActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(requireActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }

        binding.nameTv.text = PrefManager.getUserFullName()

        binding.editBtn.setOnClickListener {
            if(isEdit == true){
                binding.editBtn.text = "Edit"
                binding.changePicBtn.gone()
                isEdit = false
            }else{
                binding.editBtn.text = "Save"
                binding.changePicBtn.visible()
                isEdit = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.constrainLay1.gone()
        binding.constrainLay2.gone()
        binding.profileShimmerView.visible()
        binding.profileShimmerView.startShimmer()
        if(requireActivity().isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEduPopup(v : View){
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.education_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.add_edu-> {
                    val intent = Intent(requireActivity(), AddEducationActivity::class.java)
                    startActivity(intent)
                }
                R.id.edit_edu-> {

                }
            }
            true
        }
        popup.show()
    }

    private fun showCertificatePopup(v : View){
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.certificate_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.certificate_add-> {
                    val intent = Intent(requireActivity(), AddCertificateActivity::class.java)
                    startActivity(intent)
                }
                R.id.certificate_edit-> {

                }
            }
            true
        }
        popup.show()
    }

    private fun getProfileObserve(){
        mGetProfileViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){

                        binding.constrainLay1.visible()
                        binding.constrainLay2.visible()
                        binding.profileShimmerView.gone()
                        binding.profileShimmerView.stopShimmer()

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
                        data?.strikes?.let {
                            binding.strikeTv.text = it.toString()
                        }
                        data?.flags?.let {
                            binding.flagTv.text = it.toString()
                        }
                        data?.rewards?.let {
                            binding.rewardTv.text = it.toString()
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
                            binding.ageTv.text = it.toString()
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
                            binding.eduMenuBtn.visible()
                            binding.eduImg.gone()
                            binding.eduHtv.gone()
                            binding.addEduBtn.gone()
                            fillEducationRecycler(data?.education)
                        }else{
                            binding.educationRecycler.gone()
                            binding.showEducationHtv.gone()
                            binding.eduMenuBtn.gone()
                            binding.eduImg.visible()
                            binding.eduHtv.visible()
                            binding.addEduBtn.visible()
                        }

                        if(data?.certificate != null && data.certificate.isNotEmpty()){
                            binding.certificateRecycler.visible()
                            binding.showCertificateHtv.visible()
                            binding.certificateMenuBtn.visible()
                            binding.addCertificateBtn.gone()
                            binding.certificateHtv.gone()
                            binding.certificateImg.gone()
                            fillCertificateRecycler(data?.certificate)
                        }else{
                            binding.certificateRecycler.gone()
                            binding.showCertificateHtv.gone()
                            binding.certificateMenuBtn.gone()
                            binding.addCertificateBtn.visible()
                            binding.certificateHtv.visible()
                            binding.certificateImg.visible()
                        }

                        //profile status
                        if(outcome.data?.data?.profile_completion_status?.is_basic_info_added == 0){
                            showCompleteDialog("Please add your basic details to complete your profile","Complete now", 1)
                        }
                        else if(outcome.data?.data?.profile_completion_status?.is_documents_uploaded == 0){

                            if(outcome.data?.data?.profile_completion_status?.is_optional_info_added == 0){
                                showCompleteDialog("Please complete your profile","Complete now", 2)
                            }else{
                                showCompleteDialog("Please add your documents to complete your profile","Complete now", 3)
                            }
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

    private fun showCompleteDialog(title: String, btn_txt: String, step: Int) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.profile_completion_dialog_layout)

        val msg_tv = dialog.findViewById<TextView>(R.id.text_view_1)
        val complete = dialog.findViewById<TextView>(R.id.complete_btn)

        msg_tv.text = title
        complete.text = btn_txt

        complete.setOnClickListener {
            if(step != 4){
                dialog.dismiss()
                val intent = Intent(requireActivity(), BasicAndHomeAddressActivity::class.java)
                intent.putExtra("step", step.toString())
                startActivity(intent)
            }else{
                dialog.dismiss()
            }

        }
        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

}