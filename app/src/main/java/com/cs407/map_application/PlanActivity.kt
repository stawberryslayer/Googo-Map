package com.cs407.map_application

import android.content.Context
import android.content.Intent
import android.graphics.Color
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

        val daysCount = intent.getIntExtra("daysCount", 3) // Placeholder for the number of days
        val daysLayout = findViewById<LinearLayout>(R.id.daysLayout)

        fun Int.dpToPx(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }

        for (i in 1..daysCount) {
            val button = Button(this).apply {
                text = "Day $i"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8.dpToPx(context) // Add consistent spacing between buttons
                }
                setBackgroundResource(R.drawable.day_tab_unselected)
                setOnClickListener {
                    loadPlanForDay(i)
                    highlightButton(this, daysLayout)
                }
            }
            daysLayout.addView(button)
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



    // Function to highlight the selected button
    private fun highlightButton(selectedButton: Button, parentLayout: LinearLayout) {
        for (i in 0 until parentLayout.childCount) {
            val button = parentLayout.getChildAt(i) as Button
            button.setBackgroundResource(R.drawable.day_tab_unselected) // Reset to default style
        }
        selectedButton.setBackgroundResource(R.drawable.day_tab_selected) // Highlight selected button
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
            val duration = view.findViewById<TextView>(R.id.duration)
            val transportMode = view.findViewById<ImageView>(R.id.transportMode)

            startLocation.text = segment.startLocation
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
        if (day == 1) {
            return listOf(
                PlanSegment("Capitol Square", "10 min", "Walking"),
                PlanSegment("State Street", "5 min", "Car"),
                PlanSegment("Union South", "12 min", "Bus")
            )
        }
        if (day == 2) {
            return listOf(
                PlanSegment("Microbial Science Building","12 min", "Bus"),
                PlanSegment("Chazen Museum of Art", "5 min", "Bus"),
                PlanSegment("Wisconsin Institute of Medical Research", "9 min", "Bus"),
                PlanSegment("Wholefoods Market", "11 min", "Car")
            )
        }
        if (day == 3) {
            return listOf(
                PlanSegment("Lark at Kohl", "22 min", "Car")
            )
        }
        return listOf(
            PlanSegment("Capitol Square", "10 min", "Walking"),
            PlanSegment("State Street", "5 min", "Car"),
            PlanSegment("Union South", "12 min", "Bus")
        )

    }
}
