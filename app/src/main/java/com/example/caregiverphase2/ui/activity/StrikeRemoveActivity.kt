package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityStrikeListBinding
import com.example.caregiverphase2.databinding.ActivityStrikeRemoveBinding
import gone
import visible

class StrikeRemoveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStrikeRemoveBinding
    val disputeList: Array<String> =  arrayOf("Select Dispute reason", "Client was not home", "Client admitted into hospital", "Contact Confirmed (UPLOAD BUTTON) RECEIPTS/SCREEN SHOTS", "Shift Completed", "Check list Items Completed", "OTHER Reason")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStrikeRemoveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reasonTxt.gone()
        binding.proofHtv.gone()
        binding.uploadImgBtn.gone()
        binding.reasonHtv.gone()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.submitBtn.setOnClickListener {
            finish()
        }

        binding.disputeBtn.setOnClickListener {
            showDisputePopup(it)
        }

    }

    private fun showDisputePopup(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.dispute_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.client_not_home-> {
                    binding.reasonTxt.gone()
                    binding.proofHtv.visible()
                    binding.uploadImgBtn.visible()
                    binding.reasonHtv.visible()
                    binding.reasonHtv.text = "Client was not home"
                }
                R.id.client_at_hospital-> {
                    binding.reasonTxt.gone()
                    binding.proofHtv.visible()
                    binding.uploadImgBtn.visible()
                    binding.reasonHtv.visible()
                    binding.reasonHtv.text = "Client admitted into hospital"
                }
                R.id.contact_confirm-> {
                    binding.reasonTxt.gone()
                    binding.proofHtv.visible()
                    binding.uploadImgBtn.visible()
                    binding.reasonHtv.visible()
                    binding.reasonHtv.text = "Contact Confirmed (UPLOAD BUTTON) RECEIPTS/SCREEN SHOTS"
                }
                R.id.shift_completed-> {
                    binding.reasonTxt.gone()
                    binding.proofHtv.visible()
                    binding.uploadImgBtn.visible()
                    binding.reasonHtv.visible()
                    binding.reasonHtv.text = "Shift Completed"
                }
                R.id.checklist_completed-> {
                    binding.reasonTxt.gone()
                    binding.proofHtv.visible()
                    binding.uploadImgBtn.visible()
                    binding.reasonHtv.visible()
                    binding.reasonHtv.text = "Check list Items Completed"
                }
                R.id.other_reason-> {
                    binding.reasonTxt.visible()
                    binding.proofHtv.visible()
                    binding.uploadImgBtn.visible()
                    binding.reasonHtv.gone()
                }
            }
            true
        }
        popup.show()
    }
}