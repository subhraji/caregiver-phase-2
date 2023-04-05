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
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.DashBoardOpenJobsAdapter
import com.example.caregiverphase2.adapter.OpenBidListAdapter
import com.example.caregiverphase2.databinding.ActivityOpenJobListBinding
import com.example.caregiverphase2.databinding.ActivityQuickCallListBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PaginationScrollListener
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetOpenJobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import visible

@AndroidEntryPoint
class OpenJobListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenJobListBinding
    private lateinit var accessToken: String
    private val mGetOpenJobsViewModel: GetOpenJobsViewModel by viewModels()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page_no = 1
    lateinit var adapter: DashBoardOpenJobsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOpenJobListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        //observer
        getOPenJobsObserver()

        binding.backBtn.setOnClickListener {
            finish()
        }

        onScrollLister()
    }

    override fun onResume() {
        super.onResume()
        adapter = DashBoardOpenJobsAdapter(mutableListOf(),this)
        binding.openJobRecycler.adapter = adapter
        binding.openJobsShimmerView.visible()
        binding.openJobsShimmerView.startShimmer()
        binding.openJobRecycler.gone()
        binding.noDataLottie.gone()
        binding.noDataHtv.gone()
        page_no = 1
        mGetOpenJobsViewModel.getOPenJobs(token = accessToken, id = 0, page = page_no)
    }

    private fun onScrollLister(){
        val layoutManager = LinearLayoutManager(this)
        binding.openJobRecycler.layoutManager = layoutManager
        binding.openJobRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                page_no++
                mGetOpenJobsViewModel.getOPenJobs(token = accessToken, id = 0, page = page_no)
            }
        })
    }

    private fun getOPenJobsObserver(){
        mGetOpenJobsViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.openJobsShimmerView.gone()
                    binding.openJobsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data!!.isNotEmpty()){
                            binding.openJobRecycler.visible()
                            //fillOpenJobsRecycler(outcome.data?.data!!)
                            isLoading = false
                            adapter.add(outcome.data?.data!!)
                        }else{
                            if(page_no == 1){
                                binding.openJobRecycler.gone()
                                binding.noDataLottie.visible()
                                binding.noDataHtv.visible()
                            }
                        }
                        mGetOpenJobsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        if(outcome.data!!.httpStatusCode == 401){
                            PrefManager.clearPref()
                            startActivity(Intent(this, ChooseLoginRegActivity::class.java))
                            finish()
                        }
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
}