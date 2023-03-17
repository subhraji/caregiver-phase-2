package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.ShowEduItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_profile.Education
import gone
import visible

class ProfileEducationAdapter (private val itemList: List<Education>, private val context: Context):
    RecyclerView.Adapter<ProfileEducationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileEducationAdapter.ViewHolder {
        val itemBinding = ShowEduItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileEducationAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ProfileEducationAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: ShowEduItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Education, context: Context) {
            itemBinding.apply {
                editBtn.gone()
                instituteNameTv.text = data?.school_or_university
                courseNameTv.text = data?.degree
                durationTv.text = data?.start_year+"-"+data?.end_year

            }
        }

    }
}