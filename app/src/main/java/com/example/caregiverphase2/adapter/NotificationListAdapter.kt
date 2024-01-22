package com.example.caregiverphase2.adapter
import android.content.Context
import android.system.Os.remove
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.NotificationItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_notifications.Data
import com.example.caregiverphase2.utils.DeleteDocClickListener
import gone
import visible


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

    fun add(jobs: List<Data>) {
        itemList.addAll(jobs)
        notifyItemInserted(itemList.size-1)
    }

    fun remove(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemList.size)
    }
    override fun onBindViewHolder(holder: NotificationListAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context, deleteDocClickListener, position)
    }

    class ViewHolder(private val itemBinding: NotificationItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context, deleteDocClickListener: DeleteDocClickListener, position: Int) {
            itemBinding.apply {
                readMoreBtn.gone()
                titleTv.text = data.type
                if(data.content.length > 80){
                    contentTv.text = data.content.substring(0,80)+"..."
                    readMoreBtn.visible()
                }else{
                    contentTv.text = data.content
                }
                markReadTv.setOnClickListener {
                    deleteDocClickListener.deleteDoc(data?.notification_id,position.toString())
                }
                readMoreBtn.setOnClickListener {
                    contentTv.text = data.content
                    readMoreBtn.gone()
                }
                timeTv.text = data?.created_at
            }
        }
    }
}