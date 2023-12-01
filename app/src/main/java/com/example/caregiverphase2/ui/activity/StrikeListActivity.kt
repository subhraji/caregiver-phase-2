package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.EarningsAdapter
import com.example.caregiverphase2.adapter.StrikeListAdapter
import com.example.caregiverphase2.databinding.ActivityBasicAndHomeAddressBinding
import com.example.caregiverphase2.databinding.ActivityStrikeListBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_strikes.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetFlagsViewModel
import com.example.caregiverphase2.viewmodel.GetStrikesViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible

@AndroidEntryPoint
class StrikeListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStrikeListBinding

    private val mGetStrikesViewModel: GetStrikesViewModel by viewModels()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStrikeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        //observe
        getStrikesObserver()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.supportBtn.setOnClickListener {
            val intent = Intent(this, StrikeSystemActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.strikeRecycler.gone()
        binding.emptyLottie.gone()
        binding.jobsShimmerView.startShimmer()
        if(isConnectedToInternet()){
            mGetStrikesViewModel.getStrikes(accessToken)
        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fillStrikeRecycler(list: List<Data?>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.strikeRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = StrikeListAdapter(list,this@StrikeListActivity)
        }
    }

    private fun getStrikesObserver(){
        mGetStrikesViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.jobsShimmerView.stopShimmer()
                        binding.jobsShimmerView.gone()
                        if(outcome.data?.data != null){
                            if(outcome.data?.data!!.isNotEmpty()){
                                binding.strikeRecycler.visible()
                                binding.emptyLottie.gone()
                                fillStrikeRecycler(outcome.data?.data!!)
                            }else{
                                binding.strikeRecycler.gone()
                                binding.emptyLottie.visible()
                            }
                        }else{
                            binding.strikeRecycler.gone()
                            binding.emptyLottie.visible()
                        }
                        mGetStrikesViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mGetStrikesViewModel.navigationComplete()
                    }
                }
                is Outcome.Failure<*> -> {
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}