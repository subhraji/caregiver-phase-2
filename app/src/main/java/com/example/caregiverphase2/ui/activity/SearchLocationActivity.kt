package com.example.caregiverphase2.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivitySearchLocationBinding
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
import gone
import visible

class SearchLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchLocationBinding
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private var latitude: String = ""
    private var longitude: String = ""
    private var shortAddress: String = ""
    private var fullAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.locLay.gone()
        Places.initialize(applicationContext, getString(R.string.api_key));

        autocomplete()
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.useCurrentLocation.setOnClickListener {
            val intent = Intent(this, AskLocationActivity::class.java)
            intent.putExtra("from","other")
            startActivity(intent)
            finish()
        }
    }

    fun getCoordinate(lat0: Double, lng0: Double, dy: Long, dx: Long): LatLng? {
        val lat = lat0 + 180 / Math.PI * (dy / 6378137)
        val lng = lng0 + 180 / Math.PI * (dx / 6378137) / Math.cos(lat0)
        return LatLng(lat, lng)
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
                val latLangList = place.latLng.toString().split("(").toTypedArray()
                val final_latLangList = latLangList[1].toString().split(",").toTypedArray()
                latitude = final_latLangList[0].toString()
                longitude = final_latLangList[1].toString().substring(0, final_latLangList[1].length - 1)
                shortAddress = place.address
                fullAddress = place.name
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

}