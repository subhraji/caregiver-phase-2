package com.example.caregiverphase2.ui.fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import com.example.caregiverphase2.ui.activity.BasicAndHomeAddressActivity
import com.example.caregiverphase2.ui.activity.SearchLocationActivity
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetOPenBidsViewModel
import com.example.caregiverphase2.viewmodel.GetOpenJobsViewModel
import com.example.caregiverphase2.viewmodel.GetQuickCallViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
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

    private var latitude: String = ""
    private var longitude: String = ""
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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

        //observer
        getOPenJobsObserver()
        getOPenBidsObserver()
        getQuickCallObserver()

        val quickCallList = ArrayList<TestModel>()
        quickCallList.add(TestModel("a"))
        quickCallList.add(TestModel("b"))
        quickCallList.add(TestModel("c"))
        //fillQuickCallsRecycler(quickCallList)
        //fillOpenBidsRecycler(quickCallList)

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1527980965255-d3b416303d12?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=580&q=80") // image url
            .placeholder(R.color.dash_yellow) // any placeholder to load at start
            .centerCrop()
            .into(binding.userImageView)

        binding.profilePendingCart.setOnClickListener {
            showCompleteDialog()
        }

        binding.locBtn.setOnClickListener {
            val intent = Intent(requireActivity(), SearchLocationActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //getCurrentLocation()

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
            mGetQuickCallViewModel.getQuickCall(accessToken)

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

    private fun fillOpenJobsRecycler(list: List<Data>) {
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

    private fun showCompleteDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.profile_completion_dialog_layout)
        val complete = dialog.findViewById<TextView>(R.id.complete_btn)
        complete.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(requireActivity(), BasicAndHomeAddressActivity::class.java)
            startActivity(intent)
        }
        dialog.show()
    }


    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun getCurrentLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){

                if(ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ){
                    requestPermission()
                    return
                }


                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if(location == null){
                        Toast.makeText(requireActivity(),"Null Recieved",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireActivity(),"Get Success",Toast.LENGTH_SHORT).show()
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()

                        if(!latitude.isEmpty() && !longitude.isEmpty()){
                            if(requireActivity().isConnectedToInternet()){
                                binding.openJobsShimmerView.visible()
                                binding.openJobsShimmerView.startShimmer()
                                binding.openJobsRecycler.gone()
                                mGetOpenJobsViewModel.getOPenJobs(accessToken,0)

                                binding.openBidsShimmerView.visible()
                                binding.openBidsShimmerView.startShimmer()
                                binding.openBidsRecycler.gone()
                                mGetOPenBidsViewModel.getOpenBids(accessToken)

                                binding.quickCallShimmerView.visible()
                                binding.quickCallShimmerView.startShimmer()
                                binding.quickCallRecycler.gone()
                                mGetQuickCallViewModel.getQuickCall(accessToken)

                            }else{
                                Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(requireActivity(),"Please check your location", Toast.LENGTH_LONG).show()
                        }

                    }
                }

            }else{
                //setting open here
                Toast.makeText(requireActivity(),"Turn on the location",Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            //request permission here
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireActivity(),"Granted",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                Toast.makeText(requireActivity(),"Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }


}