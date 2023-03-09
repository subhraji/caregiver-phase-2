package com.example.caregiverphase2.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivitySearchLocationBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.UpdateLocationViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import gone
import loadingDialog
import visible

@AndroidEntryPoint
class SearchLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchLocationBinding
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private var latitude: String = ""
    private var longitude: String = ""
    private var shortAddress: String = ""
    private var fullAddress: String = ""

    private val mUpdateLocationViewModel: UpdateLocationViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        Places.initialize(applicationContext, getString(R.string.api_key));

        autocomplete()
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.useCurrentLoc.setOnClickListener {
            val intent = Intent(this, AskLocationActivity::class.java)
            intent.putExtra("from","other")
            startActivity(intent)
            finish()
        }

        binding.locLay.gone()
        binding.updateLocation.gone()

        binding.updateLocation.setOnClickListener {
            mUpdateLocationViewModel.updateLocation(latitude,longitude,accessToken)
            loader.show()
        }

        //observe
        updateLocationObserver()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun autocomplete(){
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT)
        autocompleteFragment.setCountries("USA")
        autocompleteFragment.setLocationBias(
            RectangularBounds.newInstance(
                LatLng(37.0902,95.7129),
                LatLng(37.0902,95.7129)
            )
        )
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("place2", "Place: ${place.name}, ${place.id}, ${place.address}")

                binding.locLay.visible()
                binding.updateLocation.visible()
                val latLangList = place.latLng.toString().split("(").toTypedArray()
                val final_latLangList = latLangList[1].toString().split(",").toTypedArray()
                latitude = final_latLangList[0].toString()
                longitude = final_latLangList[1].toString().substring(0, final_latLangList[1].length - 1)
                shortAddress = place.name
                fullAddress = place.address
                binding.addressTv.text = fullAddress
            }

            override fun onError(status: Status) {
                Log.i("place2", "An error occurred: $status")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("place", "Place: ${place.name}, ${place.id}, ${place.address}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("place", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateLocationObserver(){
        mUpdateLocationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()

                        PrefManager.setLatitude(latitude)
                        PrefManager.setLongitude(longitude)
                        PrefManager.setShortAddress(shortAddress)
                        PrefManager.setFullAddress(fullAddress)
                        finish()

                        mUpdateLocationViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    loader.dismiss()
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}