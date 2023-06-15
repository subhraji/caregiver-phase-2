package com.example.caregiverphase2.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetBiddedJobsViewModel
import com.example.caregiverphase2.viewmodel.GetJobLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import isConnectedToInternet

@AndroidEntryPoint
class DashMapsFragment : Fragment() {
    private var mMap: GoogleMap? = null
    private val locationArrayList: MutableList<LatLng>? = mutableListOf()

    private val mGetJobLocationViewModel: GetJobLocationViewModel by viewModels()
    private lateinit var accessToken: String

    private val callback = OnMapReadyCallback { googleMap ->

        PrefManager.getLatitude()?.let {
            PrefManager.getLongitude()?.let {
                val sydney = LatLng(PrefManager.getLatitude()!!.toDouble(),PrefManager.getLongitude()!!.toDouble())
                googleMap.addMarker(MarkerOptions()
                    .position(sydney)
                    .title("Current location")
                    .icon(BitmapFromVector(requireActivity().applicationContext,
                        R.drawable.ic_baseline_hail_24)))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f))
            }
        }

        for (i in locationArrayList!!.indices) {
            googleMap.addMarker(MarkerOptions().position(locationArrayList[i]).title("Marker"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList[i], 12f))
        }
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(
            context, vectorResId
        )

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dash_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        //observe
        getJobLocationObserve()

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        if(requireActivity().isConnectedToInternet()){
            PrefManager.getLatitude()?.let {
                PrefManager.getLongitude()?.let {
                    mGetJobLocationViewModel.getJobLocation(
                        accessToken,
                        PrefManager.getLatitude().toString(),
                        PrefManager.getLongitude().toString()
                    )
                }
            }
        }else{
            Toast.makeText(requireActivity(),"Oops!! No internet connection", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getJobLocationObserve(){
        mGetJobLocationViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            for (i in outcome.data?.data!!){
                                locationArrayList?.add(LatLng(i.latitude.toDouble(), i.longitude.toDouble()))
                            }
                        }
                        mGetJobLocationViewModel.navigationComplete()
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

}