package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.AlreadyBiddedItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_bidded_jobs.Data
import com.example.caregiverphase2.ui.activity.BiddedJobDetailsActivity
import com.example.caregiverphase2.ui.activity.JobDetailsActivity
import com.example.caregiverphase2.utils.Constants

class GetBiddedJobsAdapter (private val itemList: List<Data>, private val context: Context):
    RecyclerView.Adapter<GetBiddedJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetBiddedJobsAdapter.ViewHolder {
        val itemBinding = AlreadyBiddedItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GetBiddedJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: GetBiddedJobsAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: AlreadyBiddedItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: Data, context: Context) {
            itemBinding.apply {
                jobTitleTv.text = data?.jobTitle.toString()

                priceTv.text = "$"+data?.amount.toString()
                careTypeTv.text = data?.careItems.size.toString()+" "+data?.careType
                addressTv.text = data?.shortAddress.toString()
                hourHtv.text = data?.startTime+" - "+data?.endTime
                dateHtv.text = data?.date
                priceTv.text = "$"+data?.amount.toString()
                agencyNameTv.text = data?.companyName.toString()
                gen = ""
                for(i in data?.careItems){
                    if(gen.isEmpty()){
                        gen = i.gender+": "+i.age
                    }else{
                        gen = gen+", "+i.gender+": "+i.age
                    }
                }
                ageTv.text = gen
                rootLay.setOnClickListener {
                    val intent = Intent(context, BiddedJobDetailsActivity::class.java)
                    intent.putExtra("start_time", data?.startTime)
                    intent.putExtra("id", data?.jobId.toString())
                    intent.putExtra("job_type", "open_bid")
                    context.startActivity(intent)
                }

                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.companyPhoto) // image url
                    .placeholder(R.color.dash_yellow) // any placeholder to load at start
                    .centerCrop()
                    .into(agencyLogoImgView)
            }
        }

    }
}