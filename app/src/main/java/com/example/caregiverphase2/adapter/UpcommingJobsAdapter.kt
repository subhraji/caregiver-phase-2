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
import com.example.caregiverphase2.model.pojo.upcomming_job.Data
import com.example.caregiverphase2.ui.activity.BiddedJobDetailsActivity
import com.example.caregiverphase2.ui.activity.UpcommingJobDetailsActivity
import com.example.caregiverphase2.utils.Constants
import gone

class UpcommingJobsAdapter (private val itemList: List<Data>, private val context: Context):
    RecyclerView.Adapter<UpcommingJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcommingJobsAdapter.ViewHolder {
        val itemBinding = AlreadyBiddedItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpcommingJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: UpcommingJobsAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: AlreadyBiddedItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: Data, context: Context) {
            itemBinding.apply {
                jobStatusTv.gone()
                rightArrow.gone()
                timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.dash_green)))

                jobTitleTv.text = data?.title.toString()
                priceTv.text = "$"+data?.amount.toString()
                careTypeTv.text = data?.careItems.size.toString()+" "+data?.careType
                addressTv.text = data?.agency_address.toString()
                hourHtv.text = data?.startTime+" - "+data?.endTime
                dateHtv.text = data?.date
                priceTv.text = "$"+data?.amount.toString()
                agencyNameTv.text = data?.agencyName.toString()
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
                    val intent = Intent(context, UpcommingJobDetailsActivity::class.java)
                    context.startActivity(intent)
                }

                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.agencyPhoto) // image url
                    .placeholder(R.color.dash_yellow) // any placeholder to load at start
                    .centerCrop()
                    .into(agencyLogoImgView)
            }
        }

    }
}