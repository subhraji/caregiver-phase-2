package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.OpenJobsItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_open_jobs.Data
import com.example.caregiverphase2.ui.activity.JobDetailsActivity

class DashBoardOpenJobsAdapter (private val itemList: List<Data>,
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
                jobTitleTv.text = data?.title.toString()
                priceTv.text = "$"+data?.amount.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.address.toString()
                hourHtv.text = data?.start_time+" - "+data?.end_time
                dateHtv.text = data?.date
                priceTv.text = "$"+data?.amount.toString()
                agencyNameTv.text = "name"
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
                    val intent = Intent(context, JobDetailsActivity::class.java)
                    intent.putExtra("start_time", data?.start_time)
                    context.startActivity(intent)
                }
            }
        }
    }
}