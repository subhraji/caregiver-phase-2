package com.example.caregiverphase2.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.DashBoardOpenJobsAdapter
import com.example.caregiverphase2.adapter.DashOpenBidAdapter
import com.example.caregiverphase2.adapter.DashQuickCallsAdapter
import com.example.caregiverphase2.databinding.FragmentDashboardBinding
import com.example.caregiverphase2.model.pojo.get_open_jobs.Data
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.activity.*
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import lightStatusBar
import loadingDialog
import visible
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

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
    private val mGetProfileViewModel: GetProfileViewModel by viewModels()

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private var pageNumber = 1

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
        getProfileObserve()

        binding.profilePendingCart.setOnClickListener {
            if(requireActivity().isConnectedToInternet()){
                mGetProfileStatusViewModel.getProfileStatus(accessToken)
                loader.show()
            }else{
                Toast.makeText(requireActivity(),"Oops!! No internet connection",Toast.LENGTH_SHORT).show()
            }
        }

        binding.userImageView.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.locBtn.setOnClickListener {
            val intent = Intent(requireActivity(), SearchLocationActivity::class.java)
            startActivity(intent)
        }

        binding.ongoingCard.setOnClickListener {
            val intent = Intent(requireActivity(), OnGoingJobDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.seeAllHtv.setOnClickListener {
            val intent = Intent(requireActivity(), QuickCallListActivity::class.java)
            startActivity(intent)
        }

        binding.seeAll2Htv.setOnClickListener {
            val intent = Intent(requireActivity(), OpenBidsListActivity::class.java)
            startActivity(intent)
        }

        binding.seeAll3Htv.setOnClickListener {
            val intent = Intent(requireActivity(), OpenJobListActivity::class.java)
            startActivity(intent)
        }

        binding.dashEarningLay.setOnClickListener {
            val intent = Intent(requireActivity(), EarningsActivity::class.java)
            startActivity(intent)
        }

        binding.dashStrikeLay.setOnClickListener {
            val intent = Intent(requireActivity(), StrikeListActivity::class.java)
            startActivity(intent)
        }

        binding.dashJobLay.setOnClickListener {
            val intent = Intent(requireActivity(), JobsActivity::class.java)
            startActivity(intent)
        }

        binding.dashRewardLay.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {

        if(PrefManager.getShortAddress()!!.length >= 15){
            binding.shortAddressTv.text = PrefManager.getShortAddress()!!.substring(0,15)+" ..."
        }else{
            binding.shortAddressTv.text = PrefManager.getShortAddress()
        }

        if(requireActivity().isConnectedToInternet()){
            binding.openJobsShimmerView.visible()
            binding.openJobsShimmerView.startShimmer()
            binding.openJobsRecycler.gone()
            mGetOpenJobsViewModel.getOPenJobs(token = accessToken, id = 0, page = pageNumber)

            binding.openBidsShimmerView.visible()
            binding.openBidsShimmerView.startShimmer()
            binding.openBidsRecycler.gone()
            mGetOPenBidsViewModel.getOpenBids(accessToken, page = pageNumber)

            binding.quickCallShimmerView.visible()
            binding.quickCallShimmerView.startShimmer()
            binding.quickCallRecycler.gone()
            mGetQuickCallViewModel.getQuickCall(accessToken, 0, page = pageNumber)
            mGetOngoingJobViewModel.getOngoingJob(accessToken)
            mGetProfileViewModel.getProfile(accessToken)
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
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.openJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashBoardOpenJobsAdapter(list.toMutableList(),requireActivity())
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
                        if(outcome.data!!.httpStatusCode == 401){
                            PrefManager.clearPref()
                            startActivity(Intent(requireActivity(), ChooseLoginRegActivity::class.java))
                            requireActivity().finish()
                        }
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
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
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
        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun getProfileStatusObserver(){
        mGetProfileStatusViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        //Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        if(outcome.data?.data!!.isBasicInfoAdded == 0){
                            showCompleteDialog("Please add your basic details to complete your profile","Complete now", 1)
                        }
                        else if(outcome.data?.data!!.isDocumentsUploaded == 0){

                            if(outcome.data?.data!!.isOptionalInfoAdded == 0){
                                showCompleteDialog("Please complete your profile","Complete now", 2)
                            }else{
                                showCompleteDialog("Please add your documents to complete your profile","Complete now", 3)
                            }
                        }else if(outcome.data?.data!!.isProfileApproved == 0){
                            showCompleteDialog("Your profile is under review. It will take 24 to 48 hours to get the approval.","Ok", 4)
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

                            binding.timeLeftTv.text = "TIME LEFT : "+ LocalTime.MIN.plus(
                                Duration.ofMinutes( getDurationHour(
                                    getCurrentDate(),
                                    parseDateToddMMyyyy("${outcome.data!!.data[0].start_date} ${outcome.data!!.data[0].start_time}")!!
                                ) )
                            ).toString()

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

    private fun getProfileObserve(){
        mGetProfileViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val data = outcome.data?.data

                        data?.basic_info?.photo?.let {
                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+it) // image url
                                .placeholder(R.color.color_grey) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.userImageView)
                        }
                        mGetProfileViewModel.navigationComplete()
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


    private fun getDurationHour(startDateTime: String, endDateTime: String): Long {

        val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        var durationTotalMin = 0

        try {
            val d1: Date = sdf.parse(startDateTime)
            val d2: Date = sdf.parse(endDateTime)

            val difference_In_Time = d2.time - d1.time

            val difference_In_Seconds = (difference_In_Time / 1000)% 60

            val difference_In_Minutes = (difference_In_Time / (1000 * 60))% 60

            val difference_In_Hours = (difference_In_Time / (1000 * 60 * 60))% 24

            val difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24 * 365))

            var difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24))% 365

            val durationDay = difference_In_Days.toInt()
            val durationHour = difference_In_Hours.toInt()

            durationTotalMin = (durationHour*60)+difference_In_Minutes.toInt()


            Log.d("dateTime","duration => "+
                    difference_In_Years.toString()+
                    " years, "
                    + difference_In_Days
                    + " days, "
                    + difference_In_Hours
                    + " hours, "
                    + difference_In_Minutes
                    + " minutes, "
                    + difference_In_Seconds
                    + " seconds"
            )

        }

        // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }

        return durationTotalMin.toLong()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return sdf.format(Date())
    }

    private fun parseDateToddMMyyyy(time: String): String? {
        val inputPattern = "yyyy-MM-dd h:mm a"
        val outputPattern = "dd-MM-yyyy HH:mm:ss"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }
}