package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.CheckListItemLayoutBinding

class CheckListAdapter(private val itemList: List<String>, private val context: Context):
    RecyclerView.Adapter<CheckListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListAdapter.ViewHolder {
        val itemBinding = CheckListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CheckListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CheckListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)

    }

    class ViewHolder(private val itemBinding: CheckListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: String, context: Context) {
            itemBinding.apply {
                checkListTv.text = data
            }
        }

    }
}