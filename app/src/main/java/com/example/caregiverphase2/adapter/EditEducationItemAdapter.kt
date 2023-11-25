package com.example.caregiverphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiverphase2.databinding.EditEducationItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_profile.Education
import com.example.caregiverphase2.ui.activity.EditEducationFormActivity
import com.example.caregiverphase2.utils.DeleteDocClickListener

class EditEducationItemAdapter (private val itemList: List<Education>, private val context: Context,
                                private val deleteDocClickListener: DeleteDocClickListener):
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
        holder.bind(rowData, context, deleteDocClickListener)
    }

    class ViewHolder(private val itemBinding: EditEducationItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Education, context: Context, deleteDocClickListener: DeleteDocClickListener) {
            itemBinding.apply {
                instituteNameTv.text = data?.school_or_university
                courseNameTv.text = data?.degree
                durationTv.text = data?.start_year+"-"+data?.end_year

                deleteBtn.setOnClickListener {
                    deleteDocClickListener.deleteDoc(data?.id,"Education")
                }

                editBtn.setOnClickListener {
                    val intent = Intent(context, EditEducationFormActivity::class.java)
                    intent.putExtra("institute", data?.school_or_university)
                    intent.putExtra("subject", data?.degree)
                    intent.putExtra("start", data?.start_year)
                    intent.putExtra("end", data?.end_year)
                    intent.putExtra("id", data?.id.toString())
                    context.startActivity(intent)
                }
            }
        }

    }
}