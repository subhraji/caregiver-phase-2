package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.OpenJobsItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_jobs.Data
import com.example.caregiverphase2.ui.activity.JobDetailsActivity
import com.example.caregiverphase2.utils.Constants
import convertDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

class DashBoardOpenJobsAdapter (private val itemList: MutableList<Data>,
                                private val context: Context):
    RecyclerView.Adapter<DashBoardOpenJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashBoardOpenJobsAdapter.ViewHolder {
        val itemBinding = OpenJobsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashBoardOpenJobsAdapter.ViewHolder(itemBinding)
    }

    fun add(jobs: List<Data>) {
        itemList.addAll(jobs)
        notifyItemInserted(itemList.size-1)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashBoardOpenJobsAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: OpenJobsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String

        fun bind(data: Data, context: Context) {

            itemBinding.apply {
                rewardTv.text = data?.rewards
                jobTitleTv.text = data?.jobTitle.toString()
                priceTv.text = "$"+data?.amount.toString()
                careTypeTv.text = data?.careType
                if(data?.shortAddress.toString().length > 20){
                    addressTv.text = data?.shortAddress.toString().substring(0,19)+"..."
                }else{
                    addressTv.text = data?.shortAddress.toString()
                }
                hourHtv.text = data?.startTime+" - "+data?.endTime
                dateHtv.text = convertDate(data?.startDate)+" to "+convertDate(data?.endDate)
                priceTv.text = data?.amount.toString()
                agencyNameTv.text = data?.companyName.toString()
                distTv.text = data?.distance.toString()
                gen = ""
                for(i in data?.careItems){
                    if(gen.isEmpty()){
                        gen = i.patientName+", "+i.gender+" : "+i.age+" Yrs"
                    }else{
                        gen = gen+", "+i.gender+" : "+i.age+" Yrs"
                    }
                }
                ageTv.text = gen
                rootLay.setOnClickListener {
                    val intent = Intent(context, JobDetailsActivity::class.java)
                    intent.putExtra("start_time", data?.startTime)
                    intent.putExtra("id", data?.jobId.toString())
                    intent.putExtra("job_type", "open_job")
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