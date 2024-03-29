package com.example.caregiverphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.DocumentListItemLayoutBinding
import com.example.caregiverphase2.model.pojo.get_documents.Tuberculosi
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.DeleteDocClickListener

class TuberculosisListAdapter (private val itemList: List<Tuberculosi>,
                               private val context: Context,
                               private val docClickListener: DeleteDocClickListener
):
    RecyclerView.Adapter<TuberculosisListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TuberculosisListAdapter.ViewHolder {
        val itemBinding = DocumentListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TuberculosisListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: TuberculosisListAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context, docClickListener)

    }

    class ViewHolder(private val itemBinding: DocumentListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Tuberculosi, context: Context, docClickListener: DeleteDocClickListener) {
            itemBinding.apply {
                Glide.with(context)
                    .load(Constants.PUBLIC_URL+data?.image) // image url
                    .placeholder(R.color.color_grey) // any placeholder to load at start
                    .centerCrop()
                    .into(imageView)

                clearBtn.setOnClickListener {
                    docClickListener.deleteDoc(data?.id,"tuberculosis")
                }
            }
        }

    }
}