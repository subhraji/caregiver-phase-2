package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.CriminalListAdapter
import com.example.caregiverphase2.adapter.EarningsAdapter
import com.example.caregiverphase2.databinding.ActivityAddBioBinding
import com.example.caregiverphase2.databinding.ActivityEarningsBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_documents.Criminal

class EarningsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningsBinding
    val monthList: Array<String> =  arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEarningsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn1.setOnClickListener {
            finish()
        }

        binding.addBankBtn.setOnClickListener {
            val intent = Intent(this, AddBankAccountActivity::class.java)
            startActivity(intent)
        }

        binding.backBtn2.setOnClickListener {
            finish()
        }

        //spinner
        setUpMonthSpinner()

        val list: MutableList<TestModel> = mutableListOf()
        list.add(TestModel("a"))
        list.add(TestModel("b"))
        list.add(TestModel("c"))
        list.add(TestModel("d"))
        list.add(TestModel("e"))
        fillEarningsRecycler(list)
    }

    private fun fillEarningsRecycler(list: MutableList<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.earningsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = EarningsAdapter(list,this@EarningsActivity)
        }
    }

    private fun setUpMonthSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,monthList)
        binding.monthSpinner.adapter = arrayAdapter
        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }
}