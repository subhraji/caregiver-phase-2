package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ItemChatMeBinding
import com.example.caregiverphase2.databinding.ItemChatOtherBinding
import com.example.caregiverphase2.model.pojo.chat.ChatModel
import com.example.caregiverphase2.utils.Constants
import gone
import visible

class MessageListAdapter (private val messageList: MutableList<ChatModel>,
                          private val context: Context
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "MessageListAdapter"
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.isSender) {
            // If the current user is the sender of the message
            VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding: ViewBinding

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            itemBinding = ItemChatMeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MessageListAdapter.SentMessageHolder(itemBinding)
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            itemBinding = ItemChatOtherBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MessageListAdapter.ReceivedMessageHolder(itemBinding)
        }


        itemBinding = ItemChatMeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageListAdapter.SentMessageHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: ChatModel) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    fun addAllMessages(messages: List<ChatModel>) {
        messageList.addAll(0, messages)
        notifyItemRangeInserted(0, messages.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val rowData = messageList[position]
        /*holder.bind(rowData, context, position)*/
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                (holder as SentMessageHolder).bind(rowData, holder.itemView.context, holder, position)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as ReceivedMessageHolder).bind(rowData, holder.itemView.context, holder, position)
            }
        }
    }

    private class SentMessageHolder(private val itemBinding: ItemChatMeBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: ChatModel, context: Context, holder: RecyclerView.ViewHolder, position: Int) {
            itemBinding.apply {
                imageProgressMe.gone()
                if(!data.image.isEmpty() && data.image != null){
                    chatImgViewMe.visible()
                    Glide.with(context)
                        .load(Constants.PUBLIC_URL+data?.image) // image url
                        .placeholder(R.color.color_grey) // any placeholder to load at start
                        .centerCrop()
                        .into(chatImgViewMe)
                }else{
                    chatImgViewMe.gone()
                }
                textChatMessageMe.text = data.msg
                textChatTimestampMe.text = data.time
            }
        }
    }

    private class ReceivedMessageHolder(private val itemBinding: ItemChatOtherBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: ChatModel, context: Context, holder: RecyclerView.ViewHolder, position: Int) {
            itemBinding.apply {
                imageProgressOther.gone()
                if(!data.image.isEmpty() && data.image != null){
                    chatImgViewOther.visible()
                    Glide.with(context)
                        .load(Constants.PUBLIC_URL+data?.image) // image url
                        .placeholder(R.color.color_grey) // any placeholder to load at start
                        .centerCrop()
                        .into(chatImgViewOther)
                }else{
                    chatImgViewOther.gone()
                }

                textChatMessageOther.text = data.msg
                textGchatTimestampOther.text = data.time
            }
        }
    }
}