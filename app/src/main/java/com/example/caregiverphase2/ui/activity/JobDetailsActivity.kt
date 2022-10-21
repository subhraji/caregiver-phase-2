package com.example.caregiverphase2.ui.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAuthBinding
import com.example.caregiverphase2.databinding.ActivityJobDetailsBinding
import com.user.caregiver.gone
import com.user.caregiver.visible

class JobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickJobOverview()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.jobOverviewCard.setOnClickListener {
            clickJobOverview()
        }

        binding.checklistCard.setOnClickListener {
            clickCheckList()
        }

        binding.bidNowTv.setOnClickListener {
            showCompleteDialog()
        }
    }

    private fun clickJobOverview(){
        binding.jobOverviewTv.setBackgroundResource(R.color.theme_blue)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.checkListTv.setBackgroundResource(R.color.white)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay1.visible()
        binding.relativeLay2.gone()

    }

    private fun clickCheckList(){
        binding.checkListTv.setBackgroundResource(R.color.theme_blue)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.jobOverviewTv.setBackgroundResource(R.color.white)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay2.visible()
        binding.relativeLay1.gone()
    }

    private fun showCompleteDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.profile_completion_dialog_layout)
        val complete = dialog.findViewById<TextView>(R.id.complete_btn)
        complete.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(this, BasicAndHomeAddressActivity::class.java)
            startActivity(intent)
        }
        dialog.show()
    }
}