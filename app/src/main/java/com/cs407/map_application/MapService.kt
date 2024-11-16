package com.cs407.map_application

import com.cs407.map_application.data.AppDatabase
import com.cs407.map_application.data.Location
import com.cs407.map_application.data.Route
import com.cs407.map_application.data.RouteDao
import android.content.Context
import android.util.Log
import com.cs407.map_application.data.LocationDao
import com.cs407.map_application.model.DirectionsResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapService(
    private val context: Context
) {
    private lateinit var googleMapsApiService: GoogleMapsApiService;
    private lateinit var routeDao: RouteDao
    private lateinit var locationDao: LocationDao
    private lateinit var db: AppDatabase


//    private val db: AppDatabase = AppDatabase.getDatabase(context)


    init {
        try {
            Log.d("MapService", "Initializing database...")
            db = AppDatabase.getDatabase(context)
            routeDao = db.routeDao()
            locationDao = db.locationDao()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MapService", "Error initializing database: ${e.message}")
        }

        // Initialize Retrofit for Google Maps API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        googleMapsApiService = retrofit.create(GoogleMapsApiService::class.java)
//        routeDao = db.routeDao()
//        locationDao = db.locationDao()
    }

//    fun initializeMap(defaultLocation: LatLng, zoomLevel: Float) {
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, zoomLevel))
//        googleMap.uiSettings.isZoomControlsEnabled = true
//
//    }
//
//    fun addMarker(location: LatLng, title: String = "") {
//        googleMap.addMarker(MarkerOptions().position(location).title(title))
//    }

    fun drawRoute(start: LatLng, end: LatLng, waypoints: List<LatLng> = listOf()) {
        // Implement route drawing logic, e.g., by calling Google Directions API
    }
    private fun mapToRoute(response: DirectionsResponse, startId: Int, endId: Int): Route {
        val route = response.routes.firstOrNull()
        val leg = route?.legs?.firstOrNull()

        return Route(
            startId = startId,
            endId = endId,
            distance = leg?.distance?.value?.toDouble() ?: 0.0,
            duration = leg?.duration?.value ?: 0,
            transitInfo = "",  // Optionally, you can serialize detailed transit info here
            transportMode = "driving"
        )
    }

    fun getDirections(
        origin: String,
        destination: String,
        coroutineScope: CoroutineScope,
        onResult: (Boolean, String) -> Unit
    ) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GoogleMapsApiService::class.java)
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Fetch directions from Google Maps API
                val response = googleMapsApiService.getDirections(origin, destination, "driving", "AIzaSyB7W-JKD19WIleSOyv5aJBIzQc651vZMkU")
                if (response.isSuccessful && response.body() != null) {
                    val directionsResponse = response.body()!!

                    // Assuming you have already inserted the locations into your database:
                    val startLocation = Location(name = origin, latitude = 0.0, longitude = 0.0, timestamp = 0)  // Replace with real values
                    val endLocation = Location(name = destination, latitude = 0.0, longitude = 0.0, timestamp = 0)  // Replace with real values
//                    val startId = locationDao.insertDestination(startLocation).toInt()
//                    val endId = locationDao.insertDestination(endLocation).toInt()
//
//                    // Map response to Route entity
//                    val route = mapToRoute(directionsResponse, startId, endId)
//
//                    // Insert the Route entity into Room database
//                    routeDao.insertRoute(route)


                    withContext(Dispatchers.Main) {
                        onResult(true, "Route saved successfully.")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Failed to get directions.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(false, "An error occurred: ${e.message}")
                }
            }
        }
    }

    fun fetchDirections(start: LatLng, end: LatLng, waypoints: List<LatLng>) {
        // Make a network request to the Google Directions API
        // Process the response and provide necessary data for the UI
    }
}