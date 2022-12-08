package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.OpenBidsItemLayoutBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.ui.activity.JobDetailsActivity
import gone
import visible

class DashOpenBidAdapter(private val itemList: List<TestModel>, private val context: Context, private val isBid: Boolean):
    RecyclerView.Adapter<DashOpenBidAdapter.DashQuickCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashOpenBidAdapter.DashQuickCallsViewHolder {
        val itemBinding = OpenBidsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashOpenBidAdapter.DashQuickCallsViewHolder(itemBinding,isBid)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashOpenBidAdapter.DashQuickCallsViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class DashQuickCallsViewHolder(private val itemBinding: OpenBidsItemLayoutBinding, private val isBid: Boolean) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {

            itemBinding.apply {
                //openJobAmountTv.text = "$"+data.amount_per_hour
                if(isBid == false){
                    timeLeftTv.gone()
                }else{
                    timeLeftTv.visible()
                }

                rootLay.setOnClickListener {
                    val intent = Intent(context, JobDetailsActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }

    }
}