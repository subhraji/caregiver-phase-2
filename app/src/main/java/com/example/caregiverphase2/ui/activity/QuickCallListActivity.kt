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
import com.example.caregiverphase2.adapter.DashBoardOpenJobsAdapter
import com.example.caregiverphase2.adapter.DashQuickCallsAdapter
import com.example.caregiverphase2.adapter.QuickCallListAdapter
import com.example.caregiverphase2.databinding.ActivityMainBinding
import com.example.caregiverphase2.databinding.ActivityQuickCallListBinding
import com.example.caregiverphase2.model.pojo.get_open_jobs.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PaginationScrollListener
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetQuickCallViewModel
import dagger.hilt.android.AndroidEntryPoint
import gone
import visible

@AndroidEntryPoint
class QuickCallListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuickCallListBinding
    private val mGetQuickCallViewModel: GetQuickCallViewModel by viewModels()

    private lateinit var accessToken: String

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page_no = 1
    lateinit var adapter: QuickCallListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuickCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backBtn.setOnClickListener {
            finish()
        }

        //observe
        getQuickCallObserver()

        onScrollListener()
    }

    override fun onResume() {
        super.onResume()

        adapter = QuickCallListAdapter(mutableListOf(),this)
        binding.quickCallRecycler.adapter = adapter
        binding.quickCallShimmerView.visible()
        binding.quickCallShimmerView.startShimmer()
        binding.quickCallRecycler.gone()
        binding.noDataLottie.gone()
        binding.noDataHtv.gone()
        page_no = 1
        mGetQuickCallViewModel.getQuickCall(accessToken, 0, page = page_no)
    }

    private fun onScrollListener(){
        val layoutManager = LinearLayoutManager(this)
        binding.quickCallRecycler.layoutManager = layoutManager
        binding.quickCallRecycler.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                page_no++
                mGetQuickCallViewModel.getQuickCall(accessToken, 0, page = page_no)
            }
        })
    }

    private fun getQuickCallObserver(){
        mGetQuickCallViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.quickCallShimmerView.gone()
                    binding.quickCallShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data!!.isNotEmpty()){
                            binding.quickCallRecycler.visible()
                            isLoading = false
                            adapter.add(outcome.data?.data!!)
                        }else{
                            if(page_no == 1){
                                binding.noDataLottie.visible()
                                binding.noDataHtv.visible()
                                binding.quickCallRecycler.gone()
                            }
                        }
                        mGetQuickCallViewModel.navigationComplete()
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
}