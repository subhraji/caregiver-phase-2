package com.example.caregiverphase2.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.CriminalListAdapter
import com.example.caregiverphase2.adapter.EarningsAdapter
import com.example.caregiverphase2.databinding.ActivityAddBioBinding
import com.example.caregiverphase2.databinding.ActivityEarningsBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_documents.Criminal
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.AddBankViewModel
import com.example.caregiverphase2.viewmodel.EditBasicInfoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import isConnectedToInternet
import loadingDialog

@AndroidEntryPoint
class EarningsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningsBinding
    val monthList: Array<String> =  arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mAddBankViewModel: AddBankViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEarningsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog(true)

        binding.backBtn1.setOnClickListener {
            finish()
        }

        binding.addBankBtn.setOnClickListener {
            if(isConnectedToInternet()){
                loader.show()
                mAddBankViewModel.addBank("test4@gmail.com",accessToken)
            }else{
                Snackbar.make(binding.rootView,"Oops!! No internet connection.", Snackbar.LENGTH_SHORT).show()
            }
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

        //observer
        addBankObserver()
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

    private fun addBankObserver(){
        mAddBankViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val intent = Intent(this, WebViewActivity::class.java)
                        /*intent.putExtra("url", outcome.data!!.data.toString())
                        startActivity(intent)*/

                        val url = outcome.data!!.data.toString()
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(browserIntent)

                        mAddBankViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }
}