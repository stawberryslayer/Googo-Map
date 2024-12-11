package com.cs407.map_application

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cs407.map_application.data.AppDatabase
import com.cs407.map_application.model.Destination
import com.cs407.map_application.data.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DetailPage : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val boundsBuilder = LatLngBounds.Builder()
    //private lateinit var database: AppDatabase
    //private lateinit var locationList: List<Location>
    private lateinit var destinationList: List<Destination>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail)

        //database = AppDatabase.getDatabase(this)

        val destinationsJson = intent.getStringExtra("destinations_json") ?: "[]"
        val gson = Gson()
        val destinationType = object : TypeToken<List<Destination>>() {}.type
        destinationList = gson.fromJson(destinationsJson, destinationType)

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, PlanActivity::class.java)
            startActivity(intent)
            finish()
        }

        val downloadButton: Button = findViewById(R.id.download_button)
        downloadButton.setOnClickListener {
            if (destinationList.size >= 2) {
                for (i in 0 until destinationList.size - 1) {
                    val start = LatLng(destinationList[i].latitude, destinationList[i].longitude)
                    val end = LatLng(destinationList[i + 1].latitude, destinationList[i + 1].longitude)
                    fetchRouteDetails(start, end) { details ->
                        saveRouteToGallery(details)
                    }
                }
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

/*    private fun loadLocationsFromDatabase() {
        lifecycleScope.launch {
            locationList = withContext(Dispatchers.IO) {
                database.locationDao().getAllLocations()
            }
            onLocationsLoaded()
        }
    }

    private fun onLocationsLoaded() {
        for (location in locationList) {
            val latLng = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location.name))
            boundsBuilder.include(latLng)
        }

        if (locationList.size >= 2) {
            for (i in 0 until locationList.size - 1) {
                val start = LatLng(locationList[i].latitude, locationList[i].longitude)
                val end = LatLng(locationList[i + 1].latitude, locationList[i + 1].longitude)
                requestRoute(start, end, Color.BLUE)
            }
        }
    }
*/
override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        mMap.isMyLocationEnabled = true
        drawDestinationsAndRoutes()
    } else {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }
}

    private fun drawDestinationsAndRoutes() {
        for (destination in destinationList) {
            val latLng = LatLng(destination.latitude, destination.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(destination.name))
            boundsBuilder.include(latLng)
            updateCameraView()
        }

        if (destinationList.size >= 2) {
            for (i in 0 until destinationList.size - 1) {
                val start = LatLng(destinationList[i].latitude, destinationList[i].longitude)
                val end = LatLng(destinationList[i + 1].latitude, destinationList[i + 1].longitude)
                requestRoute(start, end, Color.BLUE)
            }
        }
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

    private fun fetchRouteDetails(start: LatLng, end: LatLng, callback: (String) -> Unit) {
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
                runOnUiThread {
                    Toast.makeText(this@DetailPage, "Failed to fetch route details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val routes = jsonObject.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val legs = routes.getJSONObject(0).getJSONArray("legs")
                        val leg = legs.getJSONObject(0)
                        val distance = leg.getJSONObject("distance").getString("text")
                        val duration = leg.getJSONObject("duration").getString("text")
                        val steps = leg.getJSONArray("steps")

                        val directions = StringBuilder("Distance: $distance\nDuration: $duration\n\nSteps:\n")
                        for (i in 0 until steps.length()) {
                            val step = steps.getJSONObject(i)
                            val instruction = step.getString("html_instructions").replace("<[^>]*>".toRegex(), "")
                            directions.append("- $instruction\n")
                        }
                        callback(directions.toString())
                    }
                }
            }
        })
    }

    private fun saveRouteToGallery(details: String) {
        val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            isAntiAlias = true
        }

        canvas.drawColor(Color.WHITE)

        val lines = details.split("\n")
        var yOffset = 50f
        for (line in lines) {
            canvas.drawText(line, 20f, yOffset, paint)
            yOffset += 60f
        }

        val fileName = "route_details_${System.currentTimeMillis()}.png"
        saveBitmapToGallery(this, bitmap, fileName)
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        try {
            val outputStream: OutputStream?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri == null) {
                    runOnUiThread {
                        Toast.makeText(context, "Failed to save route to gallery.", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                outputStream = context.contentResolver.openOutputStream(uri)
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!picturesDir.exists()) {
                    if (!picturesDir.mkdirs()) {
                        runOnUiThread {
                            Toast.makeText(context, "Failed to create directory for saving image.", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                }
                val file = File(picturesDir, fileName)
                outputStream = FileOutputStream(file)

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(file)
                context.sendBroadcast(mediaScanIntent)
            }

            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                runOnUiThread {
                    Toast.makeText(context, "Route saved to gallery!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(context, "Error saving image: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openRouteDetails(start: LatLng, end: LatLng) {
        val intent = Intent(this, RouteActivity::class.java).apply {
            putExtra("start_lat", start.latitude)
            putExtra("start_lng", start.longitude)
            putExtra("end_lat", end.latitude)
            putExtra("end_lng", end.longitude)
        }
        startActivity(intent)
    }

    private fun openInGoogleMaps(start: LatLng, end: LatLng) {
        val uri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                    "&origin=${start.latitude},${start.longitude}" +
                    "&destination=${end.latitude},${end.longitude}" +
                    "&travelmode=driving"
        )
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }
}
