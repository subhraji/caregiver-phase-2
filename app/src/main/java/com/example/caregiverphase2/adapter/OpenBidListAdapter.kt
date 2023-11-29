package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.OpenBidListItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_open_jobs.Data
import com.example.caregiverphase2.ui.activity.JobDetailsActivity
import com.example.caregiverphase2.utils.Constants
import convertDate

class OpenBidListAdapter (private val itemList: MutableList<Data>, private val context: Context):
    RecyclerView.Adapter<OpenBidListAdapter.DashQuickCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenBidListAdapter.DashQuickCallsViewHolder {
        val itemBinding = OpenBidListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OpenBidListAdapter.DashQuickCallsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun add(jobs: List<Data>) {
        itemList.addAll(jobs)
        notifyItemInserted(itemList.size-1)
    }

    override fun onBindViewHolder(holder: OpenBidListAdapter.DashQuickCallsViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class DashQuickCallsViewHolder(private val itemBinding: OpenBidListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: Data, context: Context) {
            itemBinding.apply {
                jobTitleTv.text = data?.job_title.toString()
                priceTv.text = "$"+data?.amount.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.short_address.toString()
                hourHtv.text = data?.start_time+" - "+data?.end_time
                dateHtv.text = convertDate(data?.start_date)+" to "+convertDate(data?.end_date)
                priceTv.text = data?.amount.toString()
                agencyNameTv.text = data?.company_name.toString()
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.patient_name+", "+i.gender+" : "+i.age
                    }else{
                        gen = gen+", "+i.gender+" : "+i.age
                    }
                }
                ageTv.text = gen
                rootLay.setOnClickListener {
                    val intent = Intent(context, JobDetailsActivity::class.java)
                    intent.putExtra("start_time", data?.start_time)
                    intent.putExtra("id", data?.job_id.toString())
                    intent.putExtra("job_type", "open_bid")
                    context.startActivity(intent)
                }

                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.company_photo) // image url
                    .placeholder(R.color.dash_yellow) // any placeholder to load at start
                    .centerCrop()
                    .into(agencyLogoImgView)
            }
        }

    }
}