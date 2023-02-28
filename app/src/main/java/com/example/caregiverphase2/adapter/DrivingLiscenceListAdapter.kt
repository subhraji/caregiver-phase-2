package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.DocumentListItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_documents.Driving
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.DeleteDocClickListener

class DrivingLiscenceListAdapter (private val itemList: List<Driving>,
                                  private val context: Context,
                                  private val deleteDocClickListener: DeleteDocClickListener
):
    RecyclerView.Adapter<DrivingLiscenceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrivingLiscenceListAdapter.ViewHolder {
        val itemBinding = DocumentListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DrivingLiscenceListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DrivingLiscenceListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context, deleteDocClickListener)

    }

    class ViewHolder(private val itemBinding: DocumentListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Driving, context: Context, deleteDocClickListener: DeleteDocClickListener) {
            itemBinding.apply {
                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.image) // image url
                    .placeholder(R.color.color_grey) // any placeholder to load at start
                    .centerCrop()
                    .into(imageView)

                clearBtn.setOnClickListener {
                    deleteDocClickListener.deleteDoc(data?.id)
                }
            }
        }

    }

}