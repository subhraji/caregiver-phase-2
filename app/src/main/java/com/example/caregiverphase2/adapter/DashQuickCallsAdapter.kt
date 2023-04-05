package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.QuickCallsItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_open_jobs.Data
import com.example.caregiverphase2.ui.activity.QuickCallsDetailsActivity
import com.example.caregiverphase2.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

class DashQuickCallsAdapter(private val itemList: List<Data>, private val context: Context):
    RecyclerView.Adapter<DashQuickCallsAdapter.DashQuickCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashQuickCallsAdapter.DashQuickCallsViewHolder {
        val itemBinding = QuickCallsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashQuickCallsAdapter.DashQuickCallsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashQuickCallsAdapter.DashQuickCallsViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class DashQuickCallsViewHolder(private val itemBinding: QuickCallsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: Data, context: Context) {

            val width = context.resources.displayMetrics?.widthPixels
            if (width != null) {
                val params = RecyclerView.LayoutParams((width * 0.9).toInt(),ViewGroup.LayoutParams.MATCH_PARENT)
                params.setMargins(10, 10, 10, 0)
                itemView.layoutParams = params
            }

            itemBinding.apply {

                jobTitleTv.text = data?.job_title.toString()
                priceTv.text = "$"+data?.amount.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.short_address.toString()
                hourTv.text = data?.start_time+" - "+data?.end_time
                dateHtv.text = data?.start_date
                priceTv.text = "$"+data?.amount.toString()
                agencyNameTv.text = data?.company_name.toString()
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.gender+" : "+i.age+" Yrs"
                    }else{
                        gen = gen+", "+i.gender+" : "+i.age+" Yrs"
                    }
                }
                ageTv.text = gen
                rootLay.setOnClickListener {
                    val intent = Intent(context, QuickCallsDetailsActivity::class.java)
                    intent.putExtra("start_time", data?.start_time)
                    intent.putExtra("id", data?.job_id.toString())
                    context.startActivity(intent)
                }

                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.company_photo) // image url
                    .placeholder(R.color.dash_yellow) // any placeholder to load at start
                    .centerCrop()
                    .into(agencyLogoImgView)

                timeLeftTv.text = "TIME LEFT : "+ LocalTime.MIN.plus(
                    Duration.ofMinutes( getDurationHour(
                        getCurrentDate(),
                        parseDateToddMMyyyy("${data.start_date} ${data?.start_time}")!!
                    ) )
                ).toString()
            }
        }


        private fun getDurationHour(startDateTime: String, endDateTime: String): Long {

            val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            var durationTotalMin = 0

            try {
                val d1: Date = sdf.parse(startDateTime)
                val d2: Date = sdf.parse(endDateTime)

                val difference_In_Time = d2.time - d1.time

                val difference_In_Seconds = (difference_In_Time / 1000)% 60

                val difference_In_Minutes = (difference_In_Time / (1000 * 60))% 60

                val difference_In_Hours = (difference_In_Time / (1000 * 60 * 60))% 24

                val difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24 * 365))

                var difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24))% 365

                val durationDay = difference_In_Days.toInt()
                val durationHour = difference_In_Hours.toInt()

                durationTotalMin = (durationHour*60)+difference_In_Minutes.toInt()


                Log.d("dateTime","duration => "+
                        difference_In_Years.toString()+
                        " years, "
                        + difference_In_Days
                        + " days, "
                        + difference_In_Hours
                        + " hours, "
                        + difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds"
                )

            }

            // Catch the Exception
            catch (e: ParseException) {
                e.printStackTrace()
            }

            return durationTotalMin.toLong()
        }

        private fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            return sdf.format(Date())
        }

        private fun parseDateToddMMyyyy(time: String): String? {
            val inputPattern = "yyyy-MM-dd h:mm a"
            val outputPattern = "dd-MM-yyyy HH:mm:ss"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

        private fun parseDateToText(time: String): String? {
            val inputPattern = "yyyy-MM-dd"
            val outputPattern = "M-d, YYYY"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

    }
}