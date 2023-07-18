package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
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
import com.example.caregiverphase2.model.pojo.chat.ChatSeenRequested
import com.example.caregiverphase2.model.pojo.chat.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.ChatDocPreviewFragment
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.utils.UploadDocumentListener
import com.example.caregiverphase2.viewmodel.GetAllChatViewModel
import com.example.caregiverphase2.viewmodel.UploadChatImageViewModel
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
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
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChatActivity : AppCompatActivity(), UploadDocumentListener {
    private lateinit var binding: ActivityChatBinding
    private lateinit var mMessageAdapter: MessageListAdapter
    private var mSocket: Socket? = null
    private var agency_id: String? = null
    private var job_id: String? = null
    private lateinit var accessToken: String

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private val PICK_IMAGE_DOC = 101
    private var caption: String? = null
    private var image: String? = null
    private val TAKE_PICTURE = 2

    private var page_no = 1

    private val mUploadChatImageViewModel: UploadChatImageViewModel by viewModels()
    private val mGetAllChatViewModel: GetAllChatViewModel by viewModels()
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
            job_id = intent?.getStringExtra("job_id")

            binding.chatFrgPhoneNoTxt.text = name
            Glide.with(this).load(Constants.PUBLIC_URL+photo)
                .placeholder(R.color.color_grey)
                .into(binding.userImg)

            binding.chatWithTv.text = "Chat with ${name}"
        }

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog(true)
        binding.progressBar.gone()
        binding.chatBtnSend.gone()

        if(isConnectedToInternet()){
            binding.chatShimmerView.visible()
            binding.chatShimmerView.startShimmer()
            binding.chatRecycler.gone()
            binding.progressBar.visible()
            mGetAllChatViewModel.getAllChat(accessToken,job_id!!.toInt(),page_no)
        }else{
            Toast.makeText(this,"Oops!! No internet connection", Toast.LENGTH_SHORT).show()
        }

        //observer
        uploadChatImageObserve()
        getAllChatObserve()

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
                val currentThreadTimeMillis = System.currentTimeMillis()
                val msgUuid = currentThreadTimeMillis.toString()

                //send chat message
                val sendMsg = ChatRequest(
                    messageText,
                    PrefManager.getUserId().toString(),
                    agency_id.toString(),
                    getCurrentTime(),
                    "",
                    msgUuid,
                    job_id!!,
                    accessToken
                )
                attemptSend(sendMsg)

                // add the message to view
                val message = ChatModel(
                    "",
                    0,
                    msgUuid,
                    messageText,
                    PrefManager.getUserId().toString(),
                    agency_id.toString(),
                    getCurrentTime()
                )
                message.isSender = true
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
        mSocket?.on("receiveMessage", onNewMessage)
        mSocket?.on("messageAck", ackStatusListener)
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

    private fun attemptSendSeen(request: ChatSeenRequested) {
        val gson = Gson()
        try {
            val obj = JSONObject(gson.toJson(request))
            mSocket!!.emit("isMessageSeen", obj)
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

                        // add the message to view
                        val chat = ChatModel(
                            message.image,
                            0,
                            message.messageId,
                            msg,
                            message.targetId,
                            message.userId,
                            time
                        )
                        chat.isSender = false
                        mMessageAdapter.addMessage(chat)
                        scrollToLast()
                        isMsgAvailAble()
                    }else{

                        // add the message to view
                        val chat = ChatModel(
                            "",
                            0,
                            message.messageId,
                            msg,
                            message.targetId,
                            message.userId,
                            time
                        )
                        chat.isSender = false
                        mMessageAdapter.addMessage(chat)
                        scrollToLast()
                        isMsgAvailAble()
                    }

                    //send seen ack
                    val sendSeen = ChatSeenRequested(
                        message.messageId,
                        agency_id.toString()
                    )
                    attemptSendSeen(sendSeen)

                } catch (e: JSONException) {
                    return@Runnable
                }

            })
        }
    }

    private val ackStatusListener: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            this@ChatActivity.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                try {
                    val msgId = data.getString("messageId")
                    val seenStatus = data.getString("messageSeen")
                    mMessageAdapter.updateSeen(msgId)
                } catch (e: JSONException) {
                    return@Runnable
                }
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
        gallery.type = "image/*"
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
                            val msgUuid = currentThreadTimeMillis.toString()
                            val sendMsg = ChatRequest(
                                caption,
                                PrefManager.getUserId().toString(),
                                agency_id.toString(),
                                getCurrentTime(),
                                it,
                                msgUuid,
                                job_id!!,
                                accessToken
                            )
                            attemptSend(sendMsg)

                            val message = ChatModel(
                                it,
                                0,
                                msgUuid,
                                caption,
                                agency_id.toString(),
                                PrefManager.getUserId().toString(),
                                getCurrentTime()
                            )
                            message.isSender = true
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

    private fun getAllChatObserve(){
        mGetAllChatViewModel.response.observe(this, androidx.lifecycle.Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.progressBar.gone()
                    binding.chatBtnSend.visible()
                    if(outcome.data?.success == true){
                        binding.chatShimmerView.stopShimmer()
                        binding.chatShimmerView.gone()

                        if(outcome.data?.chatModel != null && outcome.data?.chatModel?.size != 0){
                            binding.chatRecycler.visible()
                            val revResult = outcome.data?.chatModel!!.reversed()
                            for (msg in revResult){
                                msg.isSender = msg.userId.toString() == PrefManager.getUserId().toString()
                                msg.isSeen = msg.is_message_seen == 1
                            }
                            mMessageAdapter.addAllMessages(revResult)
                            binding.chatWithTv.gone()
                            scrollToLast()
                        }else{
                            binding.chatRecycler.gone()
                        }
                        mGetAllChatViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.gone()
                    binding.chatBtnSend.visible()
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
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.disconnect()
    }
}