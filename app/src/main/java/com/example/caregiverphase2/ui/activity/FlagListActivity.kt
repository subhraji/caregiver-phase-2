package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.adapter.FlagListAdapter
import com.example.caregiverphase2.adapter.StrikeListAdapter
import com.example.caregiverphase2.databinding.ActivityFlagListBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_flags.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetFlagsViewModel
import com.example.caregiverphase2.viewmodel.LogoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import visible

@AndroidEntryPoint
class FlagListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlagListBinding

    private val mGetFlagsViewModel: GetFlagsViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFlagListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        //observe
        getFlagsObserver()

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
        binding.flagRecycler.gone()
        binding.noDataLottie.gone()
        binding.jobsShimmerView.startShimmer()
        if(isConnectedToInternet()){
            mGetFlagsViewModel.getFlags(accessToken)
        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fillFlagRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.flagRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = FlagListAdapter(list,this@FlagListActivity)
        }
    }
    private fun getFlagsObserver(){
        mGetFlagsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        binding.jobsShimmerView.stopShimmer()
                        binding.jobsShimmerView.gone()
                        if(outcome.data?.data!!.isNotEmpty() && outcome.data?.data != null){
                            binding.flagRecycler.visible()
                            binding.noDataLottie.gone()
                            fillFlagRecycler(outcome.data?.data!!)
                        }else{
                            binding.flagRecycler.gone()
                            binding.noDataLottie.visible()
                        }
                        mGetFlagsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mGetFlagsViewModel.navigationComplete()
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