package com.example.caregiverphase2.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.caregiverphase2.R
import com.example.caregiverphase2.utils.PrefManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DashMapsFragment : Fragment() {
    private var mMap: GoogleMap? = null
    private val locationArrayList: MutableList<LatLng>? = mutableListOf()
    val ganeshguri = LatLng(26.1468844,91.700288)
    val ulubari = LatLng(26.1468844,91.700288)
    val khanapara = LatLng(26.1552477,91.7682784)
    val jalukbari = LatLng(26.1686385,91.7683804)
    val sixmile = LatLng(26.1329432,91.8109494)
    val locationA = LatLng(26.1233264,91.7941119)
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

            // below line is use to add marker to each location of our array list.
            googleMap.addMarker(MarkerOptions().position(locationArrayList[i]).title("Marker"))

            /*// below line is use to zoom our camera on map.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))*/

            // below line is use to move our camera to the specific location.
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

        locationArrayList?.add(ganeshguri)
        locationArrayList?.add(ulubari)
        locationArrayList?.add(khanapara)
        locationArrayList?.add(jalukbari)
        locationArrayList?.add(sixmile)
        locationArrayList?.add(locationA)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}