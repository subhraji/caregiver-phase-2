package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat.format
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.MessageListAdapter
import com.example.caregiverphase2.databinding.ActivityChatBinding
import com.example.caregiverphase2.model.pojo.chat.ChatModel
import com.example.caregiverphase2.model.pojo.chat.ChatRequest
import com.example.caregiverphase2.model.pojo.chat.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.ChatDocPreviewFragment
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.utils.UploadDocumentListener
import com.example.caregiverphase2.viewmodel.UploadChatImageViewModel
import com.google.gson.Gson
import com.google.gson.internal.bind.util.ISO8601Utils.format
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
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import isConnectedToInternet
import kotlinx.coroutines.*
import loadingDialog
import org.json.JSONException
import org.json.JSONObject
import visible
import java.io.File
import java.io.IOException
import java.lang.Runnable
import java.lang.String.format
import java.net.URISyntaxException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChatActivity : AppCompatActivity(), UploadDocumentListener {
    private lateinit var binding: ActivityChatBinding
    private lateinit var mMessageAdapter: MessageListAdapter
    private var mSocket: Socket? = null
    private var agency_id: String? = null
    private lateinit var accessToken: String
    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private val PICK_IMAGE_DOC = 101
    private var caption: String? = null
    private var image: String? = null
    private val TAKE_PICTURE = 2

    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private var dialog: AlertDialog? = null


    private val mUploadChatImageViewModel: UploadChatImageViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            agency_id = intent?.getStringExtra("agency_id")
            val name = intent?.getStringExtra("name")
            val photo = intent?.getStringExtra("photo")

            binding.chatFrgPhoneNoTxt.text = name
            Glide.with(this).load(Constants.PUBLIC_URL+photo)
                .placeholder(R.color.color_grey)
                .into(binding.userImg)

            binding.chatWithTv.text = "Chat with ${name}"
        }

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog(true)

        //observer
        uploadChatImageObserve()

        binding.chatFrgBackArrow.setOnClickListener {
            finish()
        }

        mMessageAdapter = MessageListAdapter(mutableListOf(), this)
        fillChatRecycler()

        isMsgAvailAble()

        binding.cameraBtn.setOnClickListener {
            selectImage()
        }

        binding.chatBtnSend.setOnClickListener {
            hideSoftKeyboard()
            val messageText = binding.textInput.text.toString().trim()
            if (messageText.isEmpty()) {
                binding.textInput.error = "message cannot be empty"
            }else{
                val message = ChatModel(
                    messageText,
                    "",
                    getCurrentTime(),
                    true
                )

                val sendMsg = ChatRequest(
                    messageText,
                    PrefManager.getUserId().toString(),
                    agency_id.toString(),
                    getCurrentTime(),
                    "",
                    accessToken
                )
                attemptSend(sendMsg)

                mMessageAdapter.addMessage(message)
                binding.textInput.text = null
                scrollToLast()
                isMsgAvailAble()
            }
        }
        initSocket()

        binding.textInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mSocket!!.emit("typing", true)
            }
        })
    }

    private fun initSocket(){
        try {
            mSocket = IO.socket(Constants.NODE_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        mSocket?.on("receiveMessage", onNewMessage);
        mSocket?.connect()

        //delay(10L)
        val userId = PrefManager.getUserId().toString()
        mSocket!!.emit("signin", userId)
    }

    private fun attemptSend(message: ChatRequest) {
        val gson = Gson()
        try {
            val obj = JSONObject(gson.toJson(message))
            mSocket!!.emit("sendMessage", obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private val onNewMessage: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            this@ChatActivity.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val username: String
                val msg: String
                var image: String? = null
                var time: String
                val gson = Gson()

                try {
                    //msg = data.getString("msg")
                    val messageData = data.getJSONObject("chatResponse")
                    val message = Gson().fromJson(messageData.toString(), Data::class.java)
                    msg = message.msg
                    image = message.image
                    time = message.time

                    if (!image.isEmpty() && image != null){
                        val chat = ChatModel(
                            msg,
                            message.image,
                            time,
                            false
                        )
                        mMessageAdapter.addMessage(chat)
                        scrollToLast()
                    }else{
                        val chat = ChatModel(
                            msg,
                            "",
                            time,
                            false
                        )
                        mMessageAdapter.addMessage(chat)
                        scrollToLast()
                        isMsgAvailAble()
                    }

                } catch (e: JSONException) {
                    return@Runnable
                }

                // add the message to view
                //addMessage(username, message)
            })
        }
    }

    private fun fillChatRecycler() {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.chatRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = mMessageAdapter
        }
    }

    private fun scrollToLast(){
        binding.chatRecycler.scrollToPosition((binding.chatRecycler.adapter?.itemCount ?: 1) - 1)
    }

    private fun isMsgAvailAble() {
        if(mMessageAdapter.itemCount == 0){
            binding.chatWithCard.visible()
        }else{
            binding.chatWithCard.gone()
        }
    }

    private fun getCurrentTime(): String{
        val sdf = SimpleDateFormat("hh:mm a")
        return sdf.format(Date())
    }

    //upload image

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    if(checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        dispatchCameraIntent()
                    }else{
                        requestCameraPermission()
                    }
                }
                "Choose from Gallery" -> {
                    if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        dispatchDocGalleryIntent()
                    }else{
                        requestStoragePermission()
                    }
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }

    private fun dispatchDocGalleryIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE_DOC)
    }

    private fun dispatchCameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImage()

            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                imageUri =
                    FileProvider.getUriForFile(this, Constants.FILE_PROVIDER, photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, TAKE_PICTURE)
            }
        }
    }

    private fun createImage(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageName = "JPEG_" + timeStamp + "_"
        var storeDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var tempImage = File.createTempFile(imageName, ".jpg", storeDir)
        image = tempImage.absolutePath
        return tempImage
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
        val dialogFragment = ChatDocPreviewFragment(this)
        dialogFragment.arguments = bundle
        dialogFragment.show(this.supportFragmentManager, "signature")
    }

    private fun requestCameraPermission() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.CAMERA,
            )
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    dispatchCameraIntent()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    requestCameraPermission()
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

    override fun uploadDoc(path: String, expiry: String?) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    caption = expiry
                    val file = File(path)
                    val compressedImageFile = Compressor.compress(this@ChatActivity, file)
                    val imagePart = createMultiPart("image", compressedImageFile)
                    if(isConnectedToInternet()){
                        mUploadChatImageViewModel.uploadChatImage(
                            imagePart,
                            PrefManager.getUserId().toString(),
                            accessToken
                        )
                        loader.show()
                    }else{
                        Toast.makeText(this@ChatActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadChatImageObserve(){
        mUploadChatImageViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        //Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()

                        outcome.data?.data?.let {

                            val currentThreadTimeMillis = System.currentTimeMillis()
                            val sendMsg = ChatRequest(
                                caption,
                                PrefManager.getUserId().toString(),
                                agency_id.toString(),
                                getCurrentTime(),
                                it,
                                accessToken
                            )
                            attemptSend(sendMsg)

                            val message = ChatModel(
                                caption,
                                it,
                                getCurrentTime(),
                                true
                            )
                            mMessageAdapter.addMessage(message)
                            scrollToLast()
                        }

                        mUploadChatImageViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        loader.dismiss()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
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

        if (requestCode == TAKE_PICTURE && resultCode == AppCompatActivity.RESULT_OK) {

            try {
                image?.let {
                    showDocImageDialog(image.toString(), imageUri.toString())
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

}