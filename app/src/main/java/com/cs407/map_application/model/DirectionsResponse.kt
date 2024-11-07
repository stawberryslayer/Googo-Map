// File: DirectionsResponse.kt
package com.cs407.map_application.model

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val start_address: String,
    val end_address: String,
    val steps: List<Step>
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Step(
    val html_instructions: String,
    val distance: Distance,
    val duration: Duration,
    val start_location: Location,
    val end_location: Location,
    val travel_mode: String
)

data class Location(
    val lat: Double,
    val lng: Double
)
