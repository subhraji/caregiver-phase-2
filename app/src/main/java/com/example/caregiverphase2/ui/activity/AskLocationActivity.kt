package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAskLocationBinding
import com.example.caregiverphase2.databinding.ActivityAuthBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.GetOpenJobsViewModel
import com.example.caregiverphase2.viewmodel.SubmitBidViewModel
import com.example.caregiverphase2.viewmodel.UpdateLocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import gone
import loadingDialog
import visible

@AndroidEntryPoint
class AskLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAskLocationBinding

    private var latitude: String = ""
    private var longitude: String = ""
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var from: String
    private lateinit var accessToken: String

    private val mUpdateLocationViewModel: UpdateLocationViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAskLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            from = intent?.getStringExtra("from")!!
        }
        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.useLocBtn.setOnClickListener {

            mUpdateLocationViewModel.updateLocation(latitude,longitude,accessToken)
            loader = this.loadingDialog()
            loader.show()

        }
    }

    override fun onResume() {
        binding.useLocBtn.gone()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        //observer
        updateLocationObserver()
        super.onResume()
    }

    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun getCurrentLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){

                if(ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ){
                    requestPermission()
                    return
                }


                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if(location == null){
                        Toast.makeText(this,"Please open your google map once, then retry", Toast.LENGTH_SHORT).show()
                    }else{
                        //Toast.makeText(this,"Get Success", Toast.LENGTH_SHORT).show()
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()

                        if(!latitude.isEmpty() && !longitude.isEmpty()){

                            binding.useLocBtn.visible()

                        }else{
                            Toast.makeText(this,"Please check your location", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }else{
                //setting open here
                Toast.makeText(this,"Turn on the location", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this,"Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                Toast.makeText(this,"Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLocationObserver(){
        mUpdateLocationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        if(from == "login"){
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            finish()
                        }

                        mUpdateLocationViewModel.navigationComplete()
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