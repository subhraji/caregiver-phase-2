package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.StrikeListItemLayoutBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_strikes.Data
import com.example.caregiverphase2.ui.activity.StrikeRemoveActivity

class StrikeListAdapter (private val itemList: List<Data?>,
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
        if (rowData != null) {
            holder.bind(rowData, context)
        }

    }

    class ViewHolder(private val itemBinding: StrikeListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context) {
            itemBinding.apply {
                reasonTv.text = data?.strike_reason
                startDateTimeTv.text = data?.start_date_time.toString()
                endDateTimeTv.text = data?.lift_date_time
                bannedBidTv.text = data?.banned_from_bidding
                bannedQuickCallTv.text = data?.banned_from_quick_call
                rewardLooseTv.text = data?.rewards_loose.toString()
                root.setOnClickListener {
                    val intent = Intent(context, StrikeRemoveActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }

    }
}