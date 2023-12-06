package com.example.caregiverphase2.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.NotificationItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_notifications.Data
import com.example.caregiverphase2.utils.DeleteDocClickListener

class NotificationListAdapter (private val itemList: MutableList<Data>,
                               private val context: Context,
                               private val deleteDocClickListener: DeleteDocClickListener):
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListAdapter.ViewHolder {
        val itemBinding = NotificationItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: NotificationListAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context, deleteDocClickListener)
    }

    class ViewHolder(private val itemBinding: NotificationItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context, deleteDocClickListener: DeleteDocClickListener) {
            itemBinding.apply {
                titleTv.text = data.type
                contentTv.text = data.content
                markReadTv.setOnClickListener {
                    deleteDocClickListener.deleteDoc(data?.notification_id,"notification")
                }
            }
        }
    }
}