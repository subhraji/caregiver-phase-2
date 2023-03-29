package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.submitBtn.setOnClickListener {
            finish()
        }

        //spinner
        setUpDisputeSpinner()
    }

    private fun setUpDisputeSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,disputeList)
        binding.disputeSpinner.adapter = arrayAdapter
        binding.disputeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 6){
                    binding.reasonTxt.visible()
                }else{
                    binding.reasonTxt.gone()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

}