package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.DashOpenBidAdapter
import com.example.caregiverphase2.adapter.OpenBidListAdapter
import com.example.caregiverphase2.adapter.QuickCallListAdapter
import com.example.caregiverphase2.databinding.ActivityOpenBidsListBinding
import com.example.caregiverphase2.databinding.ActivityOpenJobListBinding
import com.example.caregiverphase2.model.pojo.get_open_jobs.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PaginationScrollListener
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetOPenBidsViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import visible

@AndroidEntryPoint
class OpenBidsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenBidsListBinding

    private lateinit var accessToken: String
    private val mGetOPenBidsViewModel: GetOPenBidsViewModel by viewModels()

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page_no = 1
    lateinit var adapter: OpenBidListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOpenBidsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backBtn.setOnClickListener {
            finish()
        }

        //observe
        getOPenBidsObserver()

        onScrollListener()
    }

    override fun onResume() {
        super.onResume()
        adapter = OpenBidListAdapter(mutableListOf(),this)
        binding.openBidRecycler.adapter = adapter
        binding.openBidShimmerView.visible()
        binding.openBidShimmerView.startShimmer()
        binding.openBidRecycler.gone()
        binding.noDataHtv.gone()
        binding.noDataLottie.gone()
        page_no = 1
        mGetOPenBidsViewModel.getOpenBids(accessToken, page = page_no)
    }

    private fun onScrollListener(){
        val layoutManager = LinearLayoutManager(this)
        binding.openBidRecycler.layoutManager = layoutManager
        binding.openBidRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                page_no++
                mGetOPenBidsViewModel.getOpenBids(accessToken, page = page_no)
            }
        })
    }

    private fun getOPenBidsObserver(){
        mGetOPenBidsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.openBidShimmerView.gone()
                    binding.openBidShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data!!.isNotEmpty()){
                            binding.openBidRecycler.visible()
                            //fillOpenBidsRecycler(outcome.data?.data!!)
                            isLoading = false
                            adapter.add(outcome.data?.data!!)
                        }else{
                            if(page_no == 1){
                                binding.noDataHtv.gone()
                                binding.noDataLottie.gone()
                                binding.openBidRecycler.gone()
                            }
                        }
                        mGetOPenBidsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun fillOpenBidsRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.openBidRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = OpenBidListAdapter(list.toMutableList(),this@OpenBidsListActivity)
        }
    }
}