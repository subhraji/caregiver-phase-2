package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityEditBioBinding
import com.example.caregiverphase2.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val jobTypeList: Array<String> =  arrayOf("Select job type", "Senior Care", "Child Care", "Patient Care")
    private lateinit var job_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.searchBtn.setOnClickListener {
            val from = binding.fromTxt.text.toString()
            val to = binding.toTxt.text.toString()

            if(!job_type.isEmpty()){
                val intent = Intent(this, JobSearchResultActivity::class.java)
                intent.putExtra("job_type",job_type)
                intent.putExtra("from",from)
                intent.putExtra("to", to)
                startActivity(intent)
            }else if(job_type.isEmpty() && from.isEmpty() && to.isEmpty()){
                Toast.makeText(this, "please provide something to search the job.", Toast.LENGTH_SHORT).show()
            }else{
                if(!from.isEmpty()){
                    if(!to.isEmpty()){
                        val intent = Intent(this, JobSearchResultActivity::class.java)
                        intent.putExtra("job_type",job_type)
                        intent.putExtra("from",from)
                        intent.putExtra("to", to)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "please provide the amount range ends upto.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "please provide the amount range starts from.", Toast.LENGTH_SHORT).show()
                }
            }

        }

        //spinner
        setUpSpinner()
    }

    private fun setUpSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,jobTypeList)
        binding.careTypeSpinner.adapter = arrayAdapter

        binding.careTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 != 0){
                    job_type = jobTypeList[p2]
                }else{
                    job_type = ""
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

}