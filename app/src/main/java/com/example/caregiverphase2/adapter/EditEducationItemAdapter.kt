package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.EditEducationItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_profile.Education

class EditEducationItemAdapter (private val itemList: List<Education>, private val context: Context):
    RecyclerView.Adapter<EditEducationItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditEducationItemAdapter.ViewHolder {
        val itemBinding = EditEducationItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EditEducationItemAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: EditEducationItemAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: EditEducationItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Education, context: Context) {
            itemBinding.apply {
                instituteNameTv.text = data?.school_or_university
                courseNameTv.text = data?.degree
                durationTv.text = data?.start_year+"-"+data?.end_year

            }
        }

    }
}