package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.adapter.FlagListAdapter
import com.example.caregiverphase2.adapter.StrikeListAdapter
import com.example.caregiverphase2.databinding.ActivityFlagListBinding
import com.example.caregiverphase2.model.TestModel

class FlagListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlagListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFlagListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.supportBtn.setOnClickListener {
            val intent = Intent(this, StrikeSystemActivity::class.java)
            startActivity(intent)
        }

        val list: MutableList<TestModel> = mutableListOf()
        list.add(TestModel("a"))
        list.add(TestModel("b"))
        list.add(TestModel("c"))
        list.add(TestModel("d"))
        list.add(TestModel("e"))
        fillFlagRecycler(list)
    }

    private fun fillFlagRecycler(list: MutableList<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.flagRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = FlagListAdapter(list,this@FlagListActivity)
        }
    }
}