package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.AlreadyBiddedItemLayoutBinding
import com.example.caregiverphase2.model.TestModel

class CompletedJobsAdapter (private val itemList: List<TestModel>, private val context: Context):
    RecyclerView.Adapter<CompletedJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedJobsAdapter.ViewHolder {
        val itemBinding = AlreadyBiddedItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CompletedJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CompletedJobsAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: AlreadyBiddedItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: TestModel, context: Context) {
            itemBinding.apply {
                timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.theme_blue)))
                jobStatusTv.text = "Waiting for payment"
                timeLeftTv.text = "Completed 2 hrs ago"
            }
        }

    }
}