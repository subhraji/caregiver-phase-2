package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.StrikeListItemLayoutBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.ui.activity.StrikeRemoveActivity

class StrikeListAdapter (private val itemList: List<TestModel>,
                         private val context: Context
):
    RecyclerView.Adapter<StrikeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrikeListAdapter.ViewHolder {
        val itemBinding = StrikeListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StrikeListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: StrikeListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)

    }

    class ViewHolder(private val itemBinding: StrikeListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {
            itemBinding.apply {
                root.setOnClickListener {
                    val intent = Intent(context, StrikeRemoveActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }

    }
}