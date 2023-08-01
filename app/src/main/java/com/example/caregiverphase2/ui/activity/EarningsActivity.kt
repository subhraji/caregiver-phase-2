package com.example.caregiverphase2.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
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
import com.example.caregiverphase2.viewmodel.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class EarningsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningsBinding
    val monthList: Array<String> =  arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mAddBankViewModel: AddBankViewModel by viewModels()
    private val mConnectAccountStatusViewModel: ConnectAccountStatusViewModel by viewModels()
    private val mConnectRefreshUrlViewModel: ConnectRefreshUrlViewModel by viewModels()
    private val mGetBankDetailsViewModel: GetBankDetailsViewModel by viewModels()
    private val mDeleteBankViewModel: DeleteBankViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEarningsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog(true)
        binding.warningLay.gone()

        binding.backBtn1.setOnClickListener {
            finish()
        }

        binding.addBankBtn.setOnClickListener {
            if(isConnectedToInternet()){
                loader.show()
                mAddBankViewModel.addBank(accessToken)
            }else{
                Snackbar.make(binding.rootView,"Oops!! No internet connection.", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.activeBankBtn.setOnClickListener {
            if(isConnectedToInternet()){
                loader.show()
                mConnectRefreshUrlViewModel.connectRefreshUrl(accessToken)
            }else{
                Snackbar.make(binding.rootView,"Oops!! No internet connection.", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.backBtn2.setOnClickListener {
            finish()
        }

        binding.threeDot.setOnClickListener {
            showDeletePopup(it)
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
        connectAccountStatusObserver()
        connectRefreshUrlObserver()
        getBankDetailsObserver()
        deleteBankObserver()
    }

    override fun onResume() {
        super.onResume()
        if(isConnectedToInternet()){
            loader.show()
            mConnectAccountStatusViewModel.connectAccountAccount(accessToken)
        }else{
            Snackbar.make(binding.rootView,"Oops!! No internet connection.", Snackbar.LENGTH_SHORT).show()
        }

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
    private fun connectAccountStatusObserver(){
        mConnectAccountStatusViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null){
                            val data = outcome.data?.data
                            if (data?.is_charges_enabled == 1 && data?.is_payouts_enabled == 1){
                                binding.addBankLay.gone()
                                binding.showBankLay.visible()
                                binding.warningLay.gone()
                                loader.show()
                                mGetBankDetailsViewModel.getBankDetails(accessToken)
                            }
                        }
                    }else{
                        if(outcome.data?.data != null){
                            val data = outcome.data?.data
                            if (data?.is_charges_enabled == 0 && data?.is_payouts_enabled == 0){
                                binding.showBankLay.gone()
                                binding.addBankLay.visible()
                                binding.addBankBtn.gone()
                                binding.activeBankBtn.visible()
                                binding.warningLay.visible()
                            }
                        }else{
                            binding.showBankLay.gone()
                            binding.addBankLay.visible()
                            binding.activeBankBtn.gone()
                            binding.addBankBtn.visible()
                            binding.warningLay.gone()
                        }
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
    private fun connectRefreshUrlObserver(){
        mConnectRefreshUrlViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val url = outcome.data!!.data.toString()
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(browserIntent)
                        mConnectRefreshUrlViewModel.navigationComplete()
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
    private fun getBankDetailsObserver(){
        mGetBankDetailsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null){
                            val data = outcome.data?.data
                            data?.routingNumber?.let {
                                binding.routeNoTv.text = it
                            }
                            data?.accountNumber?.let {
                                binding.accountNoTv.text = it
                            }
                            data?.bankName?.let {
                                binding.bankNameTv.text = it
                            }
                        }
                        mGetBankDetailsViewModel.navigationComplete()
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
    private fun deleteBankObserver(){
        mDeleteBankViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        binding.addBankLay.visible()
                        binding.addBankBtn.visible()
                        binding.activeBankBtn.gone()
                        binding.showBankLay.gone()
                        binding.warningLay.gone()
                        mDeleteBankViewModel.navigationComplete()
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
    private fun showDeletePopup(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.bank_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.delete_btn-> {
                    showDeletePopUp()
                }
            }
            true
        }
        popup.show()
    }

    private fun showDeletePopUp(){
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete")
        builder.setMessage("Are you sure? \nwant to delete this bank account?")
        builder.setIcon(R.drawable.ic_baseline_delete_outline_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(isConnectedToInternet()){
                mDeleteBankViewModel.deleteBank(accessToken)
                loader = this.loadingDialog()
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.",Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: android.app.AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}