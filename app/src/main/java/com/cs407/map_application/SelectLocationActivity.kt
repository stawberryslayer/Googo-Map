package com.cs407.map_application

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.map_application.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteActivity

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var locationText: TextView
    private lateinit var confirmButton: Button
    private var selectedLocation: LatLng? = null
    private var selectedLocationName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyCE7HmQAIc9fggu5vAA8hdZeNK9-RaZKwA")
        }

        mapView = findViewById(R.id.map_view)
        locationText = findViewById(R.id.location_text)
        confirmButton = findViewById(R.id.confirm_button)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationText.setOnClickListener {
            openPlaceAutocomplete()
        }

        confirmButton.setOnClickListener {
            if (selectedLocation != null && selectedLocationName != null) {
                val resultIntent = Intent().apply {
                    putExtra("location_name", selectedLocationName)
                    putExtra("latitude", selectedLocation!!.latitude)
                    putExtra("longitude", selectedLocation!!.longitude)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please select a location first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val defaultLocation = LatLng(43.06801689107635, -89.40005063050859)//哥们家
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))

        selectedLocation?.let {
            addMarkerOnMap(it, selectedLocationName)
        }
    }

    private fun openPlaceAutocomplete() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG
        )

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            selectedLocation = place.latLng
            selectedLocationName = place.name

            if (selectedLocation != null) {
                addMarkerOnMap(selectedLocation!!, selectedLocationName)
                locationText.text = selectedLocationName ?: "Unknown Location"
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(data!!)
            Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addMarkerOnMap(location: LatLng, title: String?) {
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(location).title(title))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}

