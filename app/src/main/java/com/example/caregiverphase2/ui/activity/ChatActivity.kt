package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.MessageListAdapter
import com.example.caregiverphase2.databinding.ActivityChangePasswordBinding
import com.example.caregiverphase2.databinding.ActivityChatBinding
import com.example.caregiverphase2.model.pojo.chat.ChatModel
import com.example.caregiverphase2.model.pojo.chat.ChatRequest
import com.example.caregiverphase2.model.pojo.chat.Data
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.google.gson.Gson
import hideSoftKeyboard
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.delay
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var mMessageAdapter: MessageListAdapter
    private var mSocket: Socket? = null
    private var agency_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            agency_id = intent?.getStringExtra("agency_id")
        }

        binding.chatFrgBackArrow.setOnClickListener {
            finish()
        }

        mMessageAdapter = MessageListAdapter(mutableListOf(), this)

        /*val list: MutableList<ChatModel> = mutableListOf()
        list.add(ChatModel("hello how are you?", true))
        list.add(ChatModel("Hey long time no see, i am fine, what about you?", false))
        list.add(ChatModel("Are you ok?", true))*/
        fillChatRecycler()
        //mMessageAdapter.addAllMessages(list)

        Glide.with(this).load("https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8YXZhdGFyfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60")
            .placeholder(R.color.color_grey)
            .into(binding.userImg)

        binding.chatBtnSend.setOnClickListener {
            hideSoftKeyboard()
            val messageText = binding.textInput.text.toString().trim()
            if (messageText.isEmpty()) {
                binding.textInput.error = "message cannot be empty"
            }else{
                val message = ChatModel(
                    messageText,
                    true
                )

                val currentThreadTimeMillis = System.currentTimeMillis()
                val sendMsg = ChatRequest(
                    messageText,
                    PrefManager.getUserId().toString(),
                    agency_id.toString(),
                    currentThreadTimeMillis.toString(),
                    "",
                )
                attemptSend(sendMsg)

                mMessageAdapter.addMessage(message)
                binding.textInput.text = null
            }
        }

        initSocket()
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
                val gson = Gson()

                try {
                    //msg = data.getString("msg")
                    val messageData = data.getJSONObject("chatResponse")
                    val message = Gson().fromJson(messageData.toString(), Data::class.java)
                    msg = message.msg

                    val chat = ChatModel(
                        msg,
                        false
                    )
                    mMessageAdapter.addMessage(chat)

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
}