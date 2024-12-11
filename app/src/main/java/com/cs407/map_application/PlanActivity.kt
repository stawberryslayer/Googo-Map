package com.cs407.map_application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cs407.map_application.data.AppDatabase
import com.cs407.map_application.data.Location
import com.cs407.map_application.model.Destination
import com.cs407.map_application.model.PlanSegment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class PlanActivity : AppCompatActivity() {
    private lateinit var dayWiseDestinations: List<List<Destination>>
    private var tripDuration: Int = 1
    private var travelMode: String = "Walking"
    private lateinit var db: AppDatabase
    private lateinit var mapService: MapService
    private var currentSelectedDayIndex: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plan_page)

        db = AppDatabase.getDatabase(this)
        mapService = MapService(this)

        val sharedPreferences = getSharedPreferences("TripPrefs", Context.MODE_PRIVATE)
        tripDuration = sharedPreferences.getInt("trip_duration", 1)
        travelMode = sharedPreferences.getString("travel_mode", "Walking") ?: "Walking"
        val destinationsJson = sharedPreferences.getString("destinations_json", "[]")

        val gson = Gson()
        val destinationType = object : TypeToken<List<Destination>>() {}.type
        val destinations: List<Destination> = gson.fromJson(destinationsJson, destinationType)

        // 使用智能算法进行分配 distribute by using smart algorithm
        dayWiseDestinations = distributeDestinationsSmartly(destinations, tripDuration)

        // Initial UI
        val daysLayout = findViewById<LinearLayout>(R.id.daysLayout)

        loadPlanForDay(1, dayWiseDestinations, travelMode)

        var defaultButton: Button? = null

        fun Int.dpToPx(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }

        for (i in 1..tripDuration) {
            val button = Button(this).apply {
                text = "Day $i"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8.dpToPx(context)
                }
                setBackgroundResource(R.drawable.day_tab_unselected)
                setOnClickListener {
                    currentSelectedDayIndex = i
                    loadPlanForDay(i, dayWiseDestinations, travelMode)
                    highlightButton(this, daysLayout)
                }
            }
            if (i == 1) {
                defaultButton = button
            }
            daysLayout.addView(button)
        }

        defaultButton?.let {
            loadPlanForDay(1, dayWiseDestinations, travelMode)
            highlightButton(it, daysLayout)
        }

        val homeButton: Button = findViewById(R.id.home)
        val savedPlansButton: Button = findViewById(R.id.plan)
        val viewDetailsButton: Button = findViewById(R.id.view_details_button)

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        savedPlansButton.setOnClickListener {

        }

        viewDetailsButton.setOnClickListener {
            val gson = Gson()
            val selectedDayDestinations = dayWiseDestinations[currentSelectedDayIndex-1]
            val destinationsJson = gson.toJson(selectedDayDestinations)
            val intent = Intent(this, DetailPage::class.java)
            intent.putExtra("destinations_json", destinationsJson)
            startActivity(intent)
        }


    }

    /**
     * Smart Distribution Algorithm：
     * 1. K-Means clustering, grouping all destinations into clusters for days.
     * 2. The nearest neighbor algorithm is used to sort within each cluster.
     */
    private fun distributeDestinationsSmartly(destinations: List<Destination>, days: Int): List<List<Destination>> {
        if (destinations.isEmpty() || days <= 0) return listOf()
        if (days == 1) return listOf(destinations)

        val clusters = kMeansClustering(destinations, days, iterations = 50)
        // Do the nearest neighbor sort inside each cluster
        val result = clusters.map { cluster -> nearestNeighborOrdering(cluster) }
        return result
    }

    private fun nearestNeighborOrdering(destinations: List<Destination>): List<Destination> {
        if (destinations.size <= 1) return destinations

        // Calculate the center of gravity
        val centerLat = destinations.map { it.latitude }.average()
        val centerLng = destinations.map { it.longitude }.average()
        val center = Destination(name="Center", address="", latitude=centerLat, longitude=centerLng)

        val unvisited = destinations.toMutableList()
        val route = mutableListOf<Destination>()

        // Start at the point closest to the center of gravity
        var current = unvisited.minByOrNull { haversineDistance(center, it) }!!
        route.add(current)
        unvisited.remove(current)

        while (unvisited.isNotEmpty()) {
            val next = unvisited.minByOrNull { haversineDistance(current, it) }!!
            route.add(next)
            unvisited.remove(next)
            current = next
        }

        return route
    }

    private fun kMeansClustering(destinations: List<Destination>, k: Int, iterations: Int = 100): List<List<Destination>> {
        val centroids = destinations.shuffled().take(k).map { Pair(it.latitude, it.longitude) }.toMutableList()
        var clusters = listOf<List<Destination>>()

        repeat(iterations) {
            val newClusters = (0 until k).map { mutableListOf<Destination>() }.toMutableList()

            for (dest in destinations) {
                val cIndex = centroids.indices.minByOrNull { i ->
                    val c = centroids[i]
                    haversineDistance(dest.latitude, dest.longitude, c.first, c.second)
                } ?: 0
                newClusters[cIndex].add(dest)
            }

            val newCentroids = newClusters.map { cluster ->
                if (cluster.isEmpty()) {
                    val randomDest = destinations.random()
                    Pair(randomDest.latitude, randomDest.longitude)
                } else {
                    val avgLat = cluster.map { it.latitude }.average()
                    val avgLng = cluster.map { it.longitude }.average()
                    Pair(avgLat, avgLng)
                }
            }

            if (newCentroids == centroids) {
                clusters = newClusters
                return@repeat
            }
            centroids.clear()
            centroids.addAll(newCentroids)
            clusters = newClusters
        }

        return clusters
    }

    private fun haversineDistance(d1: Destination, d2: Destination): Double {
        return haversineDistance(d1.latitude, d1.longitude, d2.latitude, d2.longitude)
    }

    private fun haversineDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371e3
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lng2 - lng1)

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val d = R * c
        return d
    }

    private fun getDurationBetweenDestinations(start: Destination, end: Destination, mode: String): String {
        return runBlocking {
            val routeDao = db.routeDao()
            val locationDao = db.locationDao()

            val startId = ensureLocationInDB(locationDao, start)
            val endId = ensureLocationInDB(locationDao, end)

            val existingRoutes = routeDao.getRoutesBetweenDestinations(startId, endId)
                .filter { it.transportMode.equals(mode, ignoreCase = true) }

            if (existingRoutes.isNotEmpty()) {
                val durationInSec = existingRoutes.first().duration
                return@runBlocking formatDuration(durationInSec)
            } else {
                val originStr = "${start.latitude},${start.longitude}"
                val destinationStr = "${end.latitude},${end.longitude}"
                val durationValue = mapService.fetchRouteDuration(originStr, destinationStr, mode.lowercase())
                if (durationValue != null) {
                    val route = com.cs407.map_application.data.Route(
                        startId = startId,
                        endId = endId,
                        transitInfo = "",
                        distance = 0.0,
                        duration = durationValue,
                        transportMode = mode
                    )
                    routeDao.insertRoute(route)
                    return@runBlocking formatDuration(durationValue)
                } else {
                    return@runBlocking "N/A"
                }
            }
        }
    }

    private suspend fun ensureLocationInDB(locationDao: com.cs407.map_application.data.LocationDao, dest: Destination): Int {
        val allLocations = locationDao.getAllLocations()
        val existing = allLocations.find { it.name == dest.name && it.latitude == dest.latitude && it.longitude == dest.longitude }
        return if (existing != null) {
            existing.id
        } else {
            locationDao.insertLocation(Location(name = dest.name, latitude = dest.latitude, longitude = dest.longitude)).toInt()
        }
    }

    private fun formatDuration(durationSec: Int): String {
        val minutes = durationSec / 60
        val hours = minutes / 60
        return when {
            hours > 0 -> "${hours}h ${minutes % 60}m"
            minutes > 0 -> "${minutes} min"
            else -> "${durationSec} sec"
        }
    }

    private fun createPlanSegmentsForDay(dayDestinations: List<Destination>, transportMode: String): List<PlanSegment> {
        val segments = mutableListOf<PlanSegment>()

        for (i in dayDestinations.indices) {
            val start = dayDestinations[i]
            if (i < dayDestinations.size - 1) {
                val end = dayDestinations[i + 1]
                val durationText = getDurationBetweenDestinations(start, end, transportMode)
                segments.add(
                    PlanSegment(
                        startLocation = start.name,
                        duration = durationText,
                        transportMode = transportMode
                    )
                )
            } else {
                segments.add(
                    PlanSegment(
                        startLocation = start.name,
                        duration = "",
                        transportMode = ""
                    )
                )
            }
        }
        return segments
    }

    private fun highlightButton(selectedButton: Button, parentLayout: LinearLayout) {
        for (i in 0 until parentLayout.childCount) {
            val button = parentLayout.getChildAt(i) as Button
            button.setBackgroundResource(R.drawable.day_tab_unselected)
        }
        selectedButton.setBackgroundResource(R.drawable.day_tab_selected)
    }

    private fun loadPlanForDay(day: Int, dayWiseDestinations: List<List<Destination>>, travelMode: String) {
        val planContainer = findViewById<LinearLayout>(R.id.planContainer)
        planContainer.removeAllViews()

        val dayIndex = day - 1
        if (dayIndex < 0 || dayIndex >= dayWiseDestinations.size) return

        val segments = createPlanSegmentsForDay(dayWiseDestinations[dayIndex], travelMode)

        for (segment in segments) {
            val view = layoutInflater.inflate(R.layout.plan_item, planContainer, false)
            val startLocation = view.findViewById<TextView>(R.id.startLocation)
            val duration = view.findViewById<TextView>(R.id.duration)
            val transportModeImage = view.findViewById<ImageView>(R.id.transportMode)

            startLocation.text = segment.startLocation
            duration.text = segment.duration
            transportModeImage.setImageResource(
                when (segment.transportMode) {
                    "Walking" -> R.drawable.ic_walk
                    "By Car", "Car" -> R.drawable.ic_car
                    "By Bus", "Bus" -> R.drawable.ic_bus
                    else -> R.drawable.ic_default
                }
            )
            planContainer.addView(view)
        }
    }

}
