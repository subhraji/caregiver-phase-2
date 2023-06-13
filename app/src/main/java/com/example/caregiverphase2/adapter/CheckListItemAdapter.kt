package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.SummaryCheckListItemLayoutBinding
import com.example.caregiverphase2.model.TestModel

class CheckListItemAdapter(private val itemList: MutableList<TestModel>,
                           private val context: Context
): RecyclerView.Adapter<CheckListItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListItemAdapter.ViewHolder {
        val itemBinding = SummaryCheckListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CheckListItemAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CheckListItemAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: SummaryCheckListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {
            itemBinding.apply {
            }
        }
    }
}