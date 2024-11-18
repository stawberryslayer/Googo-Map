package com.cs407.map_application.model

import com.cs407.map_application.data.AppDatabase
import com.cs407.map_application.data.Location
import com.cs407.map_application.data.Route
import android.content.Context

class TripPlanner(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val locationDao = database.locationDao()
    private val routeDao = database.routeDao()

    data class DailyPlan(
        val day: Int,
        val locations: List<Location>,
        val routes: List<Route>,
        val totalDuration: Int,  // total time (minutes)
        val totalDistance: Double // total distance (kilometers)
    )

    suspend fun createTripPlan(locationIds: List<Int>, numberOfDays: Int): List<DailyPlan> {
        // 1. Get all location information
        val locations = locationIds.mapNotNull { id ->
            locationDao.getLocationById(id)
        }

        // 2. Get route information between all locations
        val routes = mutableListOf<Route>()
        for (i in 0 until locations.size - 1) {
            for (j in i + 1 until locations.size) {
                val routesBetween = routeDao.getRoutesBetweenDestinations(
                    locations[i].id,
                    locations[j].id
                )
                routes.addAll(routesBetween)
            }
        }

        // 3. Use algorithm to optimize trip schedule
        return optimizeTripSchedule(locations, routes, numberOfDays)
    }

    private fun optimizeTripSchedule(
        locations: List<Location>,
        routes: List<Route>,
        numberOfDays: Int
    ): List<DailyPlan> {
        // Create adjacency matrix to store time and distance between locations
        val n = locations.size
        val timeMatrix = Array(n) { IntArray(n) }
        val distanceMatrix = Array(n) { DoubleArray(n) }

        // Fill adjacency matrix
        routes.forEach { route ->
            val fromIndex = locations.indexOfFirst { it.id == route.startId }
            val toIndex = locations.indexOfFirst { it.id == route.endId }
            if (fromIndex != -1 && toIndex != -1) {
                timeMatrix[fromIndex][toIndex] = route.duration
                timeMatrix[toIndex][fromIndex] = route.duration // Assume same time for round trip
                distanceMatrix[fromIndex][toIndex] = route.distance
                distanceMatrix[toIndex][fromIndex] = route.distance
            }
        }

        // Implement greedy algorithm to allocate locations
        return distributeLocations(locations, routes, numberOfDays, timeMatrix, distanceMatrix)
    }

    private fun distributeLocations(
        locations: List<Location>,
        routes: List<Route>,
        numberOfDays: Int,
        timeMatrix: Array<IntArray>,
        distanceMatrix: Array<DoubleArray>
    ): List<DailyPlan> {
        val dailyPlans = mutableListOf<DailyPlan>()
        val maxTimePerDay = 8 * 60 // Assume maximum 8 hours (480 minutes) of touring per day

        // Use greedy algorithm to allocate locations to each day
        val unassignedLocations = locations.toMutableList()

        for (day in 1..numberOfDays) {
            val dailyLocations = mutableListOf<Location>()
            val dailyRoutes = mutableListOf<Route>()
            var totalTime = 0
            var totalDistance = 0.0

            while (unassignedLocations.isNotEmpty()) {
                val nextLocation = if (dailyLocations.isEmpty()) {
                    // If it's the first location of the day, choose the closest unvisited location
                    unassignedLocations.first()
                } else {
                    // Otherwise choose the closest location to the current last location
                    val lastLocation = dailyLocations.last()
                    val lastIndex = locations.indexOf(lastLocation)
                    unassignedLocations.minByOrNull { location ->
                        val nextIndex = locations.indexOf(location)
                        timeMatrix[lastIndex][nextIndex]
                    }
                } ?: break

                val newLocationIndex = locations.indexOf(nextLocation)
                if (dailyLocations.isNotEmpty()) {
                    val lastIndex = locations.indexOf(dailyLocations.last())
                    val additionalTime = timeMatrix[lastIndex][newLocationIndex]
                    val additionalDistance = distanceMatrix[lastIndex][newLocationIndex]

                    // Check if adding this location exceeds daily time limit
                    if (totalTime + additionalTime > maxTimePerDay) {
                        break
                    }

                    totalTime += additionalTime
                    totalDistance += additionalDistance

                    // Add route information
                    routes.find {
                        it.startId == dailyLocations.last().id && it.endId == nextLocation.id
                    }?.let {
                        dailyRoutes.add(it)
                    }
                }

                dailyLocations.add(nextLocation)
                unassignedLocations.remove(nextLocation)
            }

            if (dailyLocations.isNotEmpty()) {
                dailyPlans.add(
                    DailyPlan(
                        day = day,
                        locations = dailyLocations,
                        routes = dailyRoutes,
                        totalDuration = totalTime,
                        totalDistance = totalDistance
                    )
                )
            }
        }

        return dailyPlans
    }
}