package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.QuickCallsItemLayoutBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.ui.activity.ChooseLoginRegActivity
import com.example.caregiverphase2.ui.activity.JobDetailsActivity

class DashQuickCallsAdapter(private val itemList: List<TestModel>, private val context: Context):
    RecyclerView.Adapter<DashQuickCallsAdapter.DashQuickCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashQuickCallsAdapter.DashQuickCallsViewHolder {
        val itemBinding = QuickCallsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashQuickCallsAdapter.DashQuickCallsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashQuickCallsAdapter.DashQuickCallsViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class DashQuickCallsViewHolder(private val itemBinding: QuickCallsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {

            itemBinding.apply {
                rootLay.setOnClickListener {
                    val intent = Intent(context, JobDetailsActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }

    }
}