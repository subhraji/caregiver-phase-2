package com.example.caregiverphase2.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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

        binding.retryBtn.setOnClickListener {
            if(checkPermission()){
                if(isLocationEnabled()){
                    binding.retryBtn.gone()
                    if(locationCallback==null)
                        buildLocationCallback()
                    if(currentLocation==null)
                        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                }else{
                    Toast.makeText(this,"Turn on the location", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            }else{
                requestPermission()
            }

        }

        binding.helloHtv.text = "Hello, ${PrefManager.getUserFullName()} I am happy to assist you"
    }

    override fun onResume() {
        super.onResume()
        //new location code
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest()
        settingsCheck()
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

    private fun makePermissionRequest(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), REQUEST_GRANT_PERMISSION
        )
    }

    private fun requestPermission(){

        /*Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report.let {

                        if (report!!.areAllPermissionsGranted()) {
                            Toast.makeText(this@AskLocationActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                        }else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showSettingsDialog()
                        } else {
                            requestPermission()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.cancelPermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()*/


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showSettingsDialog()
        } else {
            makePermissionRequest()
        }
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setOnCancelListener {
            showSettingsDialog()
            Toast.makeText(this,"You must have to allow the permission to use the app.",Toast.LENGTH_LONG).show()
        }
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                openSettings()
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which ->

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                    dialog.dismiss()
                }else{
                    dialog.dismiss()
                    //showSettingsDialog()
                    finish()
                }

            })
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
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
                            shortAddress = "${list[0].locality}"
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
                    shortAddress = "${list[0].locality}"
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
                //Toast.makeText(this,"Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                PrefManager.setLocationStatus(false)
                Toast.makeText(this,"Location Is Required To Access The App", Toast.LENGTH_SHORT).show()
                binding.retryBtn.visible()
                requestPermission()
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

    private fun updateLocationObserver(){
        mUpdateLocationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        if(from == "login"){
                            PrefManager.setLocationStatus(true)
                            PrefManager.setLatitude(latitude)
                            PrefManager.setLongitude(longitude)
                            PrefManager.setShortAddress(shortAddress)
                            PrefManager.setFullAddress(fullAddress)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            PrefManager.setLatitude(latitude)
                            PrefManager.setLongitude(longitude)
                            PrefManager.setShortAddress(shortAddress)
                            PrefManager.setFullAddress(fullAddress)
                            finish()
                        }

                        mUpdateLocationViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        if(outcome.data!!.http_status_code == 401){
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

    override fun onDestroy() {
        super.onDestroy()

        if(locationCallback!=null)
            fusedLocationClient?.removeLocationUpdates(locationCallback);
    }

}