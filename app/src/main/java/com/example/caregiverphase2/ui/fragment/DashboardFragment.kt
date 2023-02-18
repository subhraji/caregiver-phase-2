package com.example.caregiverphase2.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.DashBoardOpenJobsAdapter
import com.example.caregiverphase2.adapter.DashOpenBidAdapter
import com.example.caregiverphase2.adapter.DashQuickCallsAdapter
import com.example.caregiverphase2.databinding.FragmentDashboardBinding
import com.example.caregiverphase2.model.TestModel
import com.example.caregiverphase2.model.pojo.get_open_jobs.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.*
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible
import java.util.ArrayList

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private val mGetOpenJobsViewModel: GetOpenJobsViewModel by viewModels()
    private val mGetOPenBidsViewModel: GetOPenBidsViewModel by viewModels()
    private val mGetQuickCallViewModel: GetQuickCallViewModel by viewModels()
    private val mGetProfileStatusViewModel: GetProfileStatusViewModel by viewModels()
    private val mGetOngoingJobViewModel: GetOngoingJobViewModel by viewModels()

    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        loader = requireActivity().loadingDialog()

        binding.ongoingAdjustView.gone()
        binding.ongoingCard.gone()
        binding.timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(requireActivity().resources.getColor(R.color.dash_green)))

        //observer
        getOPenJobsObserver()
        getOPenBidsObserver()
        getQuickCallObserver()
        getProfileStatusObserver()
        getOngoingJobObserver()

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1527980965255-d3b416303d12?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=580&q=80") // image url
            .placeholder(R.color.dash_yellow) // any placeholder to load at start
            .centerCrop()
            .into(binding.userImageView)

        binding.profilePendingCart.setOnClickListener {
            if(requireActivity().isConnectedToInternet()){
                mGetProfileStatusViewModel.getProfileStatus(accessToken)
                loader.show()
            }else{
                Toast.makeText(requireActivity(),"Oops!! No internet connection",Toast.LENGTH_SHORT).show()
            }
        }

        binding.userImageView.setOnClickListener {
            /*val intent = Intent(requireActivity(), AskLocationActivity::class.java)
            intent.putExtra("from","dash")
            startActivity(intent)*/
        }

        binding.locBtn.setOnClickListener {
            val intent = Intent(requireActivity(), SearchLocationActivity::class.java)
            startActivity(intent)
        }

        binding.ongoingCard.setOnClickListener {
            val intent = Intent(requireActivity(), OnGoingJobDetailsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {

        /*fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()*/

        if(requireActivity().isConnectedToInternet()){
            binding.openJobsShimmerView.visible()
            binding.openJobsShimmerView.startShimmer()
            binding.openJobsRecycler.gone()
            mGetOpenJobsViewModel.getOPenJobs(token = accessToken, id = 0)

            binding.openBidsShimmerView.visible()
            binding.openBidsShimmerView.startShimmer()
            binding.openBidsRecycler.gone()
            mGetOPenBidsViewModel.getOpenBids(accessToken)

            binding.quickCallShimmerView.visible()
            binding.quickCallShimmerView.startShimmer()
            binding.quickCallRecycler.gone()
            mGetQuickCallViewModel.getQuickCall(accessToken, 0)
            mGetOngoingJobViewModel.getOngoingJob(accessToken)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
        }

        super.onResume()
    }

    private fun fillQuickCallsRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.quickCallRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashQuickCallsAdapter(list,requireActivity())
        }
    }

    private fun fillOpenBidsRecycler(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.openBidsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashOpenBidAdapter(list,requireActivity(),true)
        }
    }

    private fun fillOpenJobsRecycler(list: List<com.example.caregiverphase2.model.pojo.get_jobs.Data>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.openJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashBoardOpenJobsAdapter(list,requireActivity())
        }
    }

    private fun getOPenJobsObserver(){
        mGetOpenJobsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.openJobsShimmerView.gone()
                    binding.openJobsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data!!.isNotEmpty()){
                            binding.seeAll3Htv.visible()
                            binding.openJobHtv.visible()
                            binding.openJobsRecycler.visible()
                            fillOpenJobsRecycler(outcome.data?.data!!)
                        }else{
                            binding.seeAll3Htv.gone()
                            binding.openJobHtv.gone()
                            binding.openJobsRecycler.gone()
                        }
                        mGetOpenJobsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getOPenBidsObserver(){
        mGetOPenBidsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.openBidsShimmerView.gone()
                    binding.openBidsShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data!!.isNotEmpty()){
                            binding.seeAll2Htv.visible()
                            binding.openBidHtv.visible()
                            binding.openBidsRecycler.visible()
                            fillOpenBidsRecycler(outcome.data?.data!!)
                        }else{
                            binding.seeAll2Htv.gone()
                            binding.openBidHtv.gone()
                            binding.openBidsRecycler.gone()
                        }
                        mGetOPenBidsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getQuickCallObserver(){
        mGetQuickCallViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.quickCallShimmerView.gone()
                    binding.quickCallShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data!!.isNotEmpty()){
                            binding.seeAllHtv.visible()
                            binding.quickCallHtv.visible()
                            binding.quickCallRecycler.visible()
                            fillQuickCallsRecycler(outcome.data?.data!!)
                        }else{
                            binding.seeAllHtv.gone()
                            binding.quickCallHtv.gone()
                            binding.quickCallRecycler.gone()
                        }
                        mGetQuickCallViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun showCompleteDialog(title: String, btn_txt: String, step: Int) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.profile_completion_dialog_layout)

        val msg_tv = dialog.findViewById<TextView>(R.id.text_view_1)
        val complete = dialog.findViewById<TextView>(R.id.complete_btn)

        msg_tv.text = title
        complete.text = btn_txt

        complete.setOnClickListener {
            if(step != 4){
                dialog.dismiss()
                val intent = Intent(requireActivity(), BasicAndHomeAddressActivity::class.java)
                intent.putExtra("step", step.toString())
                startActivity(intent)
            }else{
                dialog.dismiss()
            }

        }
        dialog.show()
    }

    private fun getProfileStatusObserver(){
        mGetProfileStatusViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        if(outcome.data?.data!!.isBasicInfoAdded == 0){
                            showCompleteDialog("Please add your basic details to complete your profile","Complete now", 1)
                        }else if(outcome.data?.data!!.isDocumentsUploaded == 0){
                            showCompleteDialog("Please add your documents to complete your profile","Complete now", 3)
                        }else if(outcome.data?.data!!.isProfileApproved == 0){
                            showCompleteDialog("Your profile is under approval process.","Ok", 4)
                        }
                        mGetProfileStatusViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getOngoingJobObserver(){
        mGetOngoingJobViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data!!.data.isNotEmpty()){
                            binding.ongoingCard.visible()
                            binding.ongoingAdjustView.visible()
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+outcome.data!!.data[0].agency_photo) // image url
                                .placeholder(R.color.dash_yellow) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.agencyImgView)

                            binding.ongoingTitleTv.text = outcome.data!!.data[0].title

                        }else{
                            binding.ongoingCard.gone()
                            binding.ongoingAdjustView.gone()
                        }
                        mGetOngoingJobViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}