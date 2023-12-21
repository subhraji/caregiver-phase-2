package com.example.caregiverphase2.adapter

import android.content.Context
import android.util.Log
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
                reasonTv.text = "Reason: "+data?.flag_reason
                rewardLooseTv.text = data?.rewards_loose.toString()
                strikeCountTv.text = "${data?.flag_number}/3"
                startDateTimeTv.text = data?.start_date_time.toString()
                endDateTimeTv.text = data?.lift_date_time

                var bannedBid = ""
                var bannedQuick = ""

                var bannedBidArray = data?.banned_from_bidding.split(':')
                if(bannedBidArray.size-1 <= 2){
                    bannedBid = "${bannedBidArray[0]}:${bannedBidArray[1]} hrs"
                }else{
                    bannedBid = "${bannedBidArray[0]} day, ${bannedBidArray[1]}:${bannedBidArray[2]} hrs"
                }

                var bannedQuickArray = data?.banned_from_quick_call.split(':')
                if(bannedQuickArray.size-1 <= 2){
                    bannedQuick = "${bannedQuickArray[0]}:${bannedQuickArray[1]} hrs"
                }else{
                    bannedQuick = "${bannedQuickArray[0]} day, ${bannedQuickArray[1]}:${bannedQuickArray[2]} hrs"
                }
                bannedBidTv.text = bannedBid
                bannedQuickCallTv.text = bannedQuick
            }
        }

    }
}