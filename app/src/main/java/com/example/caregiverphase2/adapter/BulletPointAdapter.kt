package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.NewBulletPointLayoutBinding

class BulletPointAdapter(private val itemList: List<String>, private val context: Context):
    RecyclerView.Adapter<BulletPointAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletPointAdapter.ViewHolder {
        val itemBinding = NewBulletPointLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BulletPointAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BulletPointAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)

    }

    class ViewHolder(private val itemBinding: NewBulletPointLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: String, context: Context) {
            itemBinding.apply {
                nameTv.text = data
            }
        }

    }
}