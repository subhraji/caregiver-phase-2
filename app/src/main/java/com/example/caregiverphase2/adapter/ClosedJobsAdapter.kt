package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.AlreadyBiddedItemLayoutBinding
import com.example.caregiverphase2.model.TestModel

class ClosedJobsAdapter (private val itemList: List<TestModel>, private val context: Context):
    RecyclerView.Adapter<ClosedJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosedJobsAdapter.ViewHolder {
        val itemBinding = AlreadyBiddedItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClosedJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ClosedJobsAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: AlreadyBiddedItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: TestModel, context: Context) {
            itemBinding.apply {
                priceTv.setTextColor(ContextCompat.getColor(context, R.color.dash_green))
                timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.dash_green)))
                jobStatusTv.text = "Payment Received"
                timeLeftTv.text = "Closed 2 hrs ago"
            }
        }

    }
}