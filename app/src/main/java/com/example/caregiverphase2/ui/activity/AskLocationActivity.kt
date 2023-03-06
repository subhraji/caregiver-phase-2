package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.caregiverphase2.databinding.ActivityAskLocationBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.UpdateLocationViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import gone
import loadingDialog
import visible
import java.util.*


@AndroidEntryPoint
class AskLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAskLocationBinding

    private var latitude: String = ""
    private var longitude: String = ""
    private var shortAddress: String = ""
    private var fullAddress: String = ""
    private lateinit var from: String
    private lateinit var accessToken: String

    private val mUpdateLocationViewModel: UpdateLocationViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    //new location code
    private val REQUEST_CHECK_SETTINGS: Int = 1
    private val REQUEST_GRANT_PERMISSION = 2
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private var currentLocation: Location? = null
    private var locationCallback: LocationCallback? = null

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

        binding.useLocBtn.gone()
        binding.retryBtn.gone()
        binding.useLocBtn.setOnClickListener {
            mUpdateLocationViewModel.updateLocation(latitude,longitude,accessToken)
            loader = this.loadingDialog()
            loader.show()
        }

        //observer
        updateLocationObserver()

        //new location code
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest()
        settingsCheck()

        binding.retryBtn.setOnClickListener {
            binding.retryBtn.gone()
            if(locationCallback==null)
                buildLocationCallback()
            if(currentLocation==null)
                fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    override fun onResume() {
        /*fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()*/
        super.onResume()
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
            ), REQUEST_GRANT_PERMISSION
        )
    }

    protected fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(5000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    fun settingsCheck() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            this,
            OnSuccessListener<LocationSettingsResponse?> { // All location settings are satisfied. The client can initialize
                // location requests here.
                Log.d("TAG", "onSuccess: settingsCheck")
                getCurrentLocation()
            })
        task.addOnFailureListener(this, OnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                Log.d("TAG", "onFailure: settingsCheck")
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        })
    }

    private fun getCurrentLocation() {
        if(checkPermission()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                    return
                }
                fusedLocationClient!!.lastLocation
                    .addOnSuccessListener(
                        this
                    ) { location ->
                        Log.d("TAG", "onSuccess: getLastLocation")
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            binding.retryBtn.gone()
                            binding.useLocBtn.visible()

                            currentLocation = location

                            val geocoder = Geocoder(this, Locale.getDefault())
                            val list: List<Address> =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            latitude = list[0].latitude.toString()
                            longitude = list[0].longitude.toString()
                            shortAddress = "${list[0].subLocality},${list[0].locality}"
                            fullAddress = list[0].getAddressLine(0)

                        } else {
                            Log.d("TAG", "location is null")
                            buildLocationCallback()

                            binding.retryBtn.visible()
                            binding.useLocBtn.gone()
                        }
                    }
            }else{
                Toast.makeText(this,"Turn on the location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermission()
        }
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    // Update UI with location data
                    currentLocation = location
                    Log.d("TAG", "onLocationResult: " + currentLocation!!.latitude)

                    val geocoder = Geocoder(this@AskLocationActivity, Locale.getDefault())
                    val list: List<Address> =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    latitude = list[0].latitude.toString()
                    longitude = list[0].longitude.toString()
                    shortAddress = "${list[0].subLocality},${list[0].locality}"
                    fullAddress = list[0].getAddressLine(0)

                    binding.retryBtn.gone()
                    binding.useLocBtn.visible()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_GRANT_PERMISSION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                Toast.makeText(this,"Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==RESULT_OK)
            getCurrentLocation()
        if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==RESULT_CANCELED)
            Toast.makeText(this, "Please enable Location service.", Toast.LENGTH_SHORT).show();
    }

    /*companion object{
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

                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        latitude = list[0].latitude.toString()
                        longitude = list[0].longitude.toString()
                        shortAddress = "${list[0].subLocality},${list[0].locality}"
                        fullAddress = list[0].getAddressLine(0)

                        if(!latitude.isEmpty() && !longitude.isEmpty()){
                            binding.useLocBtn.visible()
                        }else{
                            Toast.makeText(this,"Please check your location", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(this,"Please open your google map once, then retry", Toast.LENGTH_SHORT).show()

                        //getCurrentLocation()
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
    }*/

    private fun updateLocationObserver(){
        mUpdateLocationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        if(from == "login"){
                            PrefManager.setLatitude(latitude)
                            PrefManager.setLongitude(longitude)
                            PrefManager.setShortAddress(shortAddress)
                            PrefManager.setFullAddress(fullAddress)
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

    override fun onDestroy() {
        super.onDestroy()

        if(locationCallback!=null)
            fusedLocationClient?.removeLocationUpdates(locationCallback);
    }

}