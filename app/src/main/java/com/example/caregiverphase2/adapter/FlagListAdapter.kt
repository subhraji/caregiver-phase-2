package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.FlagListItemLayoutBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_flags.Data

class FlagListAdapter (private val itemList: List<Data>,
                       private val context: Context
):
    RecyclerView.Adapter<FlagListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagListAdapter.ViewHolder {
        val itemBinding = FlagListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FlagListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FlagListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)

    }

    class ViewHolder(private val itemBinding: FlagListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context) {
            itemBinding.apply {
                reasonTv.text = data?.flag_reason
                startDateTimeTv.text = data?.start_date_time.toString()
                endDateTimeTv.text = data?.lift_date_time
                bannedBidTv.text = data?.banned_from_bidding
                bannedQuickCallTv.text = data?.banned_from_quick_call
                rewardLooseTv.text = data?.rewards_loose.toString()
            }
        }

    }
}