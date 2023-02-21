package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.DocumentListItemLayoutBinding

class TuberculosisListAdapter (private val itemList: List<String>, private val context: Context):
    RecyclerView.Adapter<TuberculosisListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TuberculosisListAdapter.ViewHolder {
        val itemBinding = DocumentListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TuberculosisListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: TuberculosisListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)

    }

    class ViewHolder(private val itemBinding: DocumentListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: String, context: Context) {
            itemBinding.apply {

            }
        }

    }
}