package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.DocumentListItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_documents.Identification
import com.example.caregiverphase2.utils.Constants

class IdentityListAdapter (private val itemList: List<Identification>, private val context: Context):
    RecyclerView.Adapter<IdentityListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdentityListAdapter.ViewHolder {
        val itemBinding = DocumentListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IdentityListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: IdentityListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)

    }

    class ViewHolder(private val itemBinding: DocumentListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Identification, context: Context) {
            itemBinding.apply {
                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.image) // image url
                    .placeholder(R.color.color_grey) // any placeholder to load at start
                    .centerCrop()
                    .into(imageView)

            }
        }

    }

}