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
                reasonTv.text = "Reason: "+data?.strike_reason
                startDateTimeTv.text = data?.start_date_time.toString()
                endDateTimeTv.text = data?.lift_date_time
                rewardLooseTv.text = data?.rewards_loose.toString()


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

                root.setOnClickListener {
                    val intent = Intent(context, StrikeRemoveActivity::class.java)
                    intent.putExtra("data", data)
                    context.startActivity(intent)
                }
            }
        }

    }
}