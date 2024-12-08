package com.cs407.map_application

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cs407.map_application.data.AppDatabase
import com.cs407.map_application.data.Location
import com.cs407.map_application.data.LocationDao
import com.cs407.map_application.data.Route
import com.cs407.map_application.data.RouteDao
import com.cs407.map_application.model.DirectionsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapService(private val context: Context) {
    private lateinit var googleMapsApiService: GoogleMapsApiService
    private lateinit var routeDao: RouteDao
    private lateinit var locationDao: LocationDao
    private lateinit var db: AppDatabase

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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        googleMapsApiService = retrofit.create(GoogleMapsApiService::class.java)
    }

    /**
     * 从Google Directions API获取两个坐标点之间的行程持续时间(秒)
     */
    suspend fun fetchRouteDuration(origin: String, destination: String, mode: String): Int? {
        return withContext(Dispatchers.IO) {
            val apiKey = "AIzaSyB7W-JKD19WIleSOyv5aJBIzQc651vZMkU"
            val url = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=${Uri.encode(origin)}" +
                    "&destination=${Uri.encode(destination)}" +
                    "&mode=${mode}" +
                    "&key=$apiKey"

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: return@withContext null
                if (response.isSuccessful) {
                    val jsonObject = JSONObject(responseBody)
                    val routes = jsonObject.optJSONArray("routes") ?: return@withContext null
                    if (routes.length() > 0) {
                        val firstRoute = routes.getJSONObject(0)
                        val legs = firstRoute.getJSONArray("legs")
                        if (legs.length() > 0) {
                            val leg = legs.getJSONObject(0)
                            val duration = leg.getJSONObject("duration")
                            val durationValue = duration.getInt("value") // 秒数
                            return@withContext durationValue
                        }
                    }
                }
                return@withContext null
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    fun getDirections(
        origin: String,
        destination: String,
        coroutineScope: kotlinx.coroutines.CoroutineScope,
        onResult: (Boolean, String) -> Unit
    ) {
        // 此函数保留原有逻辑，如有需要可自行实现
    }

    fun drawRoute(start: com.google.android.gms.maps.model.LatLng, end: com.google.android.gms.maps.model.LatLng, waypoints: List<com.google.android.gms.maps.model.LatLng> = listOf()) {
        // 此处可实现绘制路线的逻辑，如果需要的话
    }

    private fun mapToRoute(response: DirectionsResponse, startId: Int, endId: Int): Route? {
        if (response.routes.isEmpty()) return null
        val route = response.routes[0]
        val leg = route.legs.firstOrNull() ?: return null
        val totalDistance = leg.distance.value.toDouble()
        val totalDuration = leg.duration.value

        val gson = com.google.gson.Gson()
        val transitInfo = gson.toJson(leg)

        val returnRoute = Route(
            startId = startId,
            endId = endId,
            transitInfo = transitInfo,
            distance = totalDistance,
            duration = totalDuration,
            transportMode = "driving"
        )
        Log.d("RouteConversion", "Return a route: $returnRoute")
        return returnRoute
    }
}
