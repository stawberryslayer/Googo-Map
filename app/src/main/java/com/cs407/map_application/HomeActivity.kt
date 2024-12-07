package com.cs407.map_application

import android.content.Intent
import android.net.Uri
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import com.cs407.map_application.data.AppDatabase
import com.cs407.map_application.data.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class HomeActivity : AppCompatActivity() {

    private var tripDuration: Int = 1 // Default trip duration
    private val maxDuration: Int = 7 // Maximum trip duration
    private lateinit var hintText: TextView
    private val REQUEST_CODE_MAP = 1
    private val travelModes = arrayOf("By Bus", "Walking", "By Car")
    private var currentModeIndex = 0
    private var locationList: MutableList<Location> = mutableListOf()


    private lateinit var destinationList: LinearLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val database = AppDatabase.getDatabase(this)

        val tripDurationTextView: TextView = findViewById(R.id.trip_duration)
        val incrementButton: Button = findViewById(R.id.increment_button)
        val decrementButton: Button = findViewById(R.id.decrement_button)
        val startButton: Button = findViewById(R.id.start_button)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Hide the app title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize the trip duration display
        updateTripDurationDisplay(tripDurationTextView)

        incrementButton.setOnClickListener {
            if (tripDuration < maxDuration) {
                tripDuration++
                updateTripDurationDisplay(tripDurationTextView)
            } else {
                Toast.makeText(this, "Plan for under $maxDuration Days!", Toast.LENGTH_SHORT).show()
            }
        }

        decrementButton.setOnClickListener {
            if (tripDuration > 1) {
                tripDuration--
                updateTripDurationDisplay(tripDurationTextView)
            } else {
                // Show toast if trying to decrement below 1, while keeping the button clickable
                Toast.makeText(this, "Your trip is at least one day long!", Toast.LENGTH_SHORT).show()
            }
        }

        // Initial button color setup
        updateButtonColors(incrementButton, decrementButton)

        val travelModeText: TextView = findViewById(R.id.travel_mode_text)
        val leftArrowButton: Button = findViewById(R.id.left_arrow_button)
        val rightArrowButton: Button = findViewById(R.id.right_arrow_button)

        // Initialize with the default travel mode
        travelModeText.text = travelModes[currentModeIndex]

        // Left arrow button click listener
        leftArrowButton.setOnClickListener {
            if (currentModeIndex > 0) {
                currentModeIndex--
            } else {
                currentModeIndex = travelModes.size - 1 // Loop back to the last option
            }
            travelModeText.text = travelModes[currentModeIndex]
        }

        // Right arrow button click listener
        rightArrowButton.setOnClickListener {
            if (currentModeIndex < travelModes.size - 1) {
                currentModeIndex++
            } else {
                currentModeIndex = 0 // Loop back to the first option
            }
            travelModeText.text = travelModes[currentModeIndex]
        }

        destinationList = findViewById(R.id.destination_list)
        val addDestinationButton: Button = findViewById(R.id.add_destination_button)

        addDestinationButton.setOnClickListener {
            val intent = Intent(this, SelectLocationActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MAP)
        }


        // Initialize buttons
        val homeButton: Button = findViewById(R.id.home)
        val savedPlansButton: Button = findViewById(R.id.plan)

        // Click listener for "Home Page" button
        savedPlansButton.setOnClickListener {
            val intent = Intent(this, PlanActivity::class.java)
            startActivity(intent)
            finish() // Close current activity to prevent stack buildup
        }

        // Click listener for "Saved Plans" button (do nothing)
        homeButton.setOnClickListener {
            // Stay on the current page
        }


        startButton.setOnClickListener{

            val sharedPreferences = this.getSharedPreferences("TripPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putInt("trip_duration", tripDuration).apply()
            sharedPreferences.edit().putString("travel_mode", travelModes[currentModeIndex]).apply()

            val locationDao = database.locationDao()

            CoroutineScope(Dispatchers.IO).launch {
                for (location in locationList) {
                    locationDao.insertLocation(location)
                }
            }
            val intent = Intent(this, PlanActivity::class.java)
            startActivity(intent)
            finish() // Close current activity to prevent stack buildup
        }

    }


    fun openGoogleMapsWithSelectedRoute(origin: String, destination: String) {
        val apiKey = "AIzaSyB7W-JKD19WIleSOyv5aJBIzQc651vZMkU"
        val url =
            "https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$destination&mode=transit&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@HomeActivity,
                        "Failed to fetch route details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val jsonData = response.body()?.string()
                        val jsonObject = JSONObject(jsonData ?: "")
                        val routes = jsonObject.optJSONArray("routes")
                        if (routes != null && routes.length() > 0) {
                            val firstRoute = routes.getJSONObject(0)
                            val legs = firstRoute.getJSONArray("legs")
                            if (legs.length() > 0) {
                                val steps = legs.getJSONObject(0).getJSONArray("steps")
                                val stepDetails = StringBuilder()
                                for (i in 0 until steps.length()) {
                                    val step = steps.getJSONObject(i)
                                    val instruction = step.getString("html_instructions")
                                    stepDetails.append("${i + 1}. $instruction\n")
                                }
                                runOnUiThread {
                                    AlertDialog.Builder(this@HomeActivity)
                                        .setTitle("Route Steps")
                                        .setMessage(stepDetails.toString())
                                        .setPositiveButton("OK", null)
                                        .show()
                                }
                            }
                        }
                    }
                }
            }

        })
    }

    fun openGoogleMaps(origin: String, destination: String) {
        val googleMapsUrl = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&travelmode=driving"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
        intent.setPackage("com.google.android.apps.maps") // Ensure it opens in Google Maps app if installed

        // Check if there's an app that can handle the intent
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            val locationName = data?.getStringExtra("location_name") ?: "New Destination"
            val latitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val longitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0

            val destinationView = LayoutInflater.from(this).inflate(R.layout.destination_item, destinationList, false)

            val destinationNameText: TextView = destinationView.findViewById(R.id.destination_name)
            destinationNameText.text = "$locationName (Lat: $latitude, Lng: $longitude)"

            locationList.add(Location(name = locationName, latitude = latitude, longitude = longitude))
            val deleteButton: Button = destinationView.findViewById(R.id.delete_button)
            deleteButton.setOnClickListener {
                destinationList.removeView(destinationView)
                // If no destinations are left, show the hint text again
                if (destinationList.childCount == 0) {
                    findViewById<TextView>(R.id.hint_text).visibility = View.VISIBLE
                }
            }

            destinationList.addView(destinationView)

            if (destinationList.childCount > 0) {
                findViewById<TextView>(R.id.hint_text).visibility = View.GONE
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.option_one -> {
                Toast.makeText(this, "Navigating to start plan...", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.option_two -> {
                Toast.makeText(this, "Navigating to saved plans...", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun updateTripDurationDisplay(tripDurationTextView: TextView) {
        tripDurationTextView.text = "$tripDuration Day(s)"
        updateButtonColors(findViewById(R.id.increment_button), findViewById(R.id.decrement_button))
    }

    private fun updateButtonColors(incrementButton: Button, decrementButton: Button) {
        // Set increment button color based on max limit
        incrementButton.backgroundTintList = if (tripDuration >= maxDuration) {
            resources.getColorStateList(android.R.color.darker_gray)
        } else {
            resources.getColorStateList(android.R.color.holo_blue_light)
        }

        // Set decrement button color based on min limit (Day 1), but keep it enabled
        decrementButton.backgroundTintList = resources.getColorStateList(
            if (tripDuration <= 1) android.R.color.darker_gray else android.R.color.holo_blue_light
        )
    }



}


