package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.MessageListAdapter
import com.example.caregiverphase2.databinding.ActivityChangePasswordBinding
import com.example.caregiverphase2.databinding.ActivityChatBinding
import com.example.caregiverphase2.model.pojo.chat.ChatModel
import hideSoftKeyboard

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var mMessageAdapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatFrgBackArrow.setOnClickListener {
            finish()
        }

        mMessageAdapter = MessageListAdapter(mutableListOf(), this)

        val list: MutableList<ChatModel> = mutableListOf()
        list.add(ChatModel("hello how are you?", true))
        list.add(ChatModel("Hey long time no see, i am fine, what about you?", false))
        list.add(ChatModel("Are you ok?", true))
        fillChatRecycler()
        mMessageAdapter.addAllMessages(list)

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
                mMessageAdapter.addMessage(message)
                binding.textInput.text = null
            }
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