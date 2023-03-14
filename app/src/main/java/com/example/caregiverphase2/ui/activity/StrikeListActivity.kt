package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.EarningsAdapter
import com.example.caregiverphase2.adapter.StrikeListAdapter
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.databinding.ActivityStrikeListBinding
import com.example.caregiverphase2.model.TestModel

class StrikeListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStrikeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStrikeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        val list: MutableList<TestModel> = mutableListOf()
        list.add(TestModel("a"))
        list.add(TestModel("b"))
        list.add(TestModel("c"))
        list.add(TestModel("d"))
        list.add(TestModel("e"))
        fillStrikeRecycler(list)
    }

    private fun fillStrikeRecycler(list: MutableList<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.strikeRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = StrikeListAdapter(list,this@StrikeListActivity)
        }
    }
}