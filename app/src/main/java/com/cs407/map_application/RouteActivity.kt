package com.cs407.map_application

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class RouteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_page)

        val startLat = intent.getDoubleExtra("start_lat", 0.0)
        val startLng = intent.getDoubleExtra("start_lng", 0.0)
        val endLat = intent.getDoubleExtra("end_lat", 0.0)
        val endLng = intent.getDoubleExtra("end_lng", 0.0)

        val routeInfoTextView: TextView = findViewById(R.id.route_info)

        fetchRouteDetails(startLat, startLng, endLat, endLng) { details ->
            runOnUiThread {
                routeInfoTextView.text = details
            }
        }
    }

    private fun fetchRouteDetails(startLat: Double, startLng: Double, endLat: Double, endLng: Double, callback: (String) -> Unit) {
        val apiKey = "AIzaSyB7W-JKD19WIleSOyv5aJBIzQc651vZMkU"
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=$startLat,$startLng&destination=$endLat,$endLng&key=$apiKey"

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
}
