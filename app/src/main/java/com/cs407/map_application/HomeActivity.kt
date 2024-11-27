package com.cs407.map_application

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HomeActivity : AppCompatActivity() {

    private var tripDuration: Int = 1 // Default trip duration
    private val maxDuration: Int = 7 // Maximum trip duration
    private lateinit var hintText: TextView
    private val REQUEST_CODE_MAP = 1
    private val travelModes = arrayOf("By Bus", "Walking", "By Car")
    private var currentModeIndex = 0

    private lateinit var destinationList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tripDurationTextView: TextView = findViewById(R.id.trip_duration)
        val incrementButton: Button = findViewById(R.id.increment_button)
        val decrementButton: Button = findViewById(R.id.decrement_button)

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

            val deleteButton: Button = destinationView.findViewById(R.id.delete_button)
            deleteButton.setOnClickListener {
                destinationList.removeView(destinationView)
            }

            destinationList.addView(destinationView)
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

    private fun addDestination(name: String) {
        // Inflate the destination_item layout
        val destinationView = LayoutInflater.from(this).inflate(R.layout.destination_item, destinationList, false)

        // Set the destination name
        val destinationName: TextView = destinationView.findViewById(R.id.destination_name)
        destinationName.text = name

        // Find the delete button in the destination_item layout
        val deleteButton: Button = destinationView.findViewById(R.id.delete_button)

        // Set the click listener for the delete button
        deleteButton.setOnClickListener {
            // Remove the destination view from the list
            destinationList.removeView(destinationView)
        }

        // Add the new destination view to the list
        destinationList.addView(destinationView)
    }

}


