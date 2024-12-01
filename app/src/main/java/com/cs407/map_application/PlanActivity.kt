package com.cs407.map_application

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PlanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call the parent onCreate method
        setContentView(R.layout.plan_page) // Set the correct layout resource

        // Find views by their IDs
        val day1Button: Button = findViewById(R.id.day1_button)
        val day2Button: Button = findViewById(R.id.day2_button)

        // Set click listeners for each button
        day1Button.setOnClickListener {
            day1Button.setBackgroundResource(R.drawable.day_tab_selected)
            day2Button.setBackgroundResource(R.drawable.day_tab_unselected)
        }

        day2Button.setOnClickListener {
            day2Button.setBackgroundResource(R.drawable.day_tab_selected)
            day1Button.setBackgroundResource(R.drawable.day_tab_unselected)
        }

        // Initialize buttons
        val homeButton: Button = findViewById(R.id.home)
        val savedPlansButton: Button = findViewById(R.id.plan)

        // Click listener for "Home Page" button
        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Close current activity to prevent stack buildup
        }

        // Click listener for "Saved Plans" button (do nothing)
        savedPlansButton.setOnClickListener {
            // Stay on the current page
        }
    }



}