package com.cs407.map_application

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

import com.google.maps.android.PolyUtil
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class DetailPage : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Test addresses in Madison, WI
    private val stateCapitol = LatLng(43.0747, -89.3844)
    private val memorialUnion = LatLng(43.0766, -89.3995)
    private val henryVilasZoo = LatLng(43.0616, -89.4091)

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail)

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            showRoutes()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        mMap.addMarker(MarkerOptions().position(stateCapitol).title("State Capitol"))
        mMap.addMarker(MarkerOptions().position(memorialUnion).title("Memorial Union"))
        mMap.addMarker(MarkerOptions().position(henryVilasZoo).title("Henry Vilas Zoo"))
    }

    private fun showRoutes() {
        requestRoute(stateCapitol, memorialUnion, Color.BLUE)

        requestRoute(memorialUnion, henryVilasZoo, Color.RED)
    }

    private fun requestRoute(start: LatLng, end: LatLng, color: Int) {
        val apiKey = "AIzaSyB7W-JKD19WIleSOyv5aJBIzQc651vZMkU"
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${start.latitude},${start.longitude}" +
                "&destination=${end.latitude},${end.longitude}" +
                "&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val routes = jsonObject.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val overviewPolyline = routes.getJSONObject(0)
                            .getJSONObject("overview_polyline")
                            .getString("points")

                        val decodedPath: List<LatLng> = PolyUtil.decode(overviewPolyline)

                        for (point in decodedPath) {
                            boundsBuilder.include(point)
                        }

                        runOnUiThread {
                            val polyline = mMap.addPolyline(
                                PolylineOptions()
                                    .addAll(decodedPath)
                                    .color(color)
                                    .width(5f)
                            )
                            updateCameraView()

                            polyline.tag = Pair(start, end)
                            polyline.isClickable = true

                            mMap.setOnPolylineClickListener { clickedPolyline ->
                                val points = clickedPolyline.tag as? Pair<LatLng, LatLng>
                                points?.let {
                                    //openRouteDetails(it.first, it.second)
                                    openInGoogleMaps(it.first, it.second)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updateCameraView() {
        val bounds = boundsBuilder.build()
        val padding = 100
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.animateCamera(cameraUpdate)
    }

/*    private fun openRouteDetails(start: LatLng, end: LatLng) {
        // 废案  Try to describe the route in words
        val intent = Intent(this, RouteActivity::class.java).apply {
            putExtra("start_lat", start.latitude)
            putExtra("start_lng", start.longitude)
            putExtra("end_lat", end.latitude)
            putExtra("end_lng", end.longitude)
        }
        startActivity(intent)
    }
*/

    private fun openInGoogleMaps(start: LatLng, end: LatLng) {
        val uri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                    "&origin=${start.latitude},${start.longitude}" +
                    "&destination=${end.latitude},${end.longitude}" +
                    "&travelmode=driving" // We can use driving, walking, bicycling, or transit
        )
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }
}
