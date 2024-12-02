package com.cs407.map_application

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cs407.map_application.model.PlanSegment

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

    private fun loadPlanForDay(day: Int) {
        val planContainer = findViewById<LinearLayout>(R.id.planContainer)
        planContainer.removeAllViews() // Clear previous day's plan (if any)

        val placeholderData = generatePlaceholderData(day) // Placeholder data for the plan

        for (segment in placeholderData) {
            // Inflate the plan_item layout
            val view = layoutInflater.inflate(R.layout.plan_item, planContainer, false)

            // Bind data to the view
            val startLocation = view.findViewById<TextView>(R.id.startLocation)
            val endLocation = view.findViewById<TextView>(R.id.endLocation)
            val duration = view.findViewById<TextView>(R.id.duration)
            val transportMode = view.findViewById<ImageView>(R.id.transportMode)

            startLocation.text = segment.startLocation
            endLocation.text = segment.endLocation
            duration.text = segment.duration
            transportMode.setImageResource(
                when (segment.transportMode) {
                    "Walking" -> R.drawable.ic_walk
                    "Car" -> R.drawable.ic_car
                    "Bus" -> R.drawable.ic_bus
                    else -> R.drawable.ic_default_transport
                }
            )

            // Add the inflated view to the LinearLayout
            planContainer.addView(view)
        }
    }

    private fun generatePlaceholderData(day: Int): List<PlanSegment> {
        // Placeholder data for demonstration
        return listOf(
            PlanSegment("Capitol Square", "State Street", "10 min", "Walking"),
            PlanSegment("State Street", "Union South", "5 min", "Car"),
            PlanSegment("Union South", "Trader Joe's", "12 min", "Bus")
        )
    }





}
