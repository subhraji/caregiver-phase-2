package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.CertificateItemLayBinding
import com.example.caregiverphase2.model.pojo.get_profile.Certificate
import gone

class ShowCertificateAdapter (private val itemList: List<Certificate>, private val context: Context):
    RecyclerView.Adapter<ShowCertificateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCertificateAdapter.ViewHolder {
        val itemBinding = CertificateItemLayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShowCertificateAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ShowCertificateAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: CertificateItemLayBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Certificate, context: Context) {
            itemBinding.apply {
                editBtn.gone()
                certificateNameTv.text = data?.certificate_or_course
                durationTv.text = data?.start_year+" to "+data?.end_year
            }
        }

    }
}