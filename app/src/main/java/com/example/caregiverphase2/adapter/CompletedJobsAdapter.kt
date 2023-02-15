package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.AlreadyBiddedItemLayoutBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_complete_job.Data
import com.example.caregiverphase2.ui.activity.BiddedJobDetailsActivity
import com.example.caregiverphase2.utils.Constants

class CompletedJobsAdapter (private val itemList: List<Data>, private val context: Context):
    RecyclerView.Adapter<CompletedJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedJobsAdapter.ViewHolder {
        val itemBinding = AlreadyBiddedItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CompletedJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CompletedJobsAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: AlreadyBiddedItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: Data, context: Context) {
            itemBinding.apply {
                timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.theme_blue)))
                jobStatusTv.text = "Waiting for payment"
                timeLeftTv.text = "Completed 2 hrs ago"

                jobTitleTv.text = data?.title.toString()
                priceTv.text = "$"+data?.amount.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.short_address.toString()
                hourHtv.text = data?.start_time+" - "+data?.end_time
                dateHtv.text = data?.date
                priceTv.text = "$"+data?.amount.toString()
                agencyNameTv.text = data?.agency_name.toString()
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.gender+": "+i.age
                    }else{
                        gen = gen+", "+i.gender+": "+i.age
                    }
                }
                ageTv.text = gen
                rootLay.setOnClickListener {
                    /*val intent = Intent(context, BiddedJobDetailsActivity::class.java)
                    intent.putExtra("start_time", data?.startTime)
                    intent.putExtra("id", data?.jobId.toString())
                    intent.putExtra("job_type", "open_bid")
                    context.startActivity(intent)*/
                }

                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.agency_photo) // image url
                    .placeholder(R.color.dash_yellow) // any placeholder to load at start
                    .centerCrop()
                    .into(agencyLogoImgView)
            }
        }

    }
}