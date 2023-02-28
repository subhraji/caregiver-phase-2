package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.DocumentListItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_documents.Covid
import com.example.caregiverphase2.utils.Constants

class CovidListAdapter (private val itemList: List<Covid>, private val context: Context):
    RecyclerView.Adapter<CovidListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CovidListAdapter.ViewHolder {
        val itemBinding = DocumentListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CovidListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CovidListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)

    }

    class ViewHolder(private val itemBinding: DocumentListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Covid, context: Context) {
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