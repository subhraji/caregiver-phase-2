package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.EditCertificateItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_profile.Certificate

class EditCertificateItemAdapter (private val itemList: List<Certificate>, private val context: Context):
    RecyclerView.Adapter<EditCertificateItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditCertificateItemAdapter.ViewHolder {
        val itemBinding = EditCertificateItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EditCertificateItemAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: EditCertificateItemAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: EditCertificateItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Certificate, context: Context) {
            itemBinding.apply {
                certificateNameTv.text = data?.certificate_or_course
                durationTv.text = data?.start_year+"-"+data?.end_year
            }
        }

    }
}