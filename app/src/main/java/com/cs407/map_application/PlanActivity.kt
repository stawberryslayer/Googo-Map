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
import com.cs407.map_application.model.Destination
import com.cs407.map_application.model.PlanSegment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlanActivity : AppCompatActivity() {
    private lateinit var dayWiseDestinations: List<List<Destination>>
    private var tripDuration: Int = 1
    private var travelMode: String = "Walking"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plan_page)

        val daysLayout = findViewById<LinearLayout>(R.id.daysLayout)

        val sharedPreferences = getSharedPreferences("TripPrefs", Context.MODE_PRIVATE)
        tripDuration = sharedPreferences.getInt("trip_duration", 1)
        travelMode = sharedPreferences.getString("travel_mode", "Walking") ?: "Walking"
        val destinationsJson = sharedPreferences.getString("destinations_json", "[]")

        val gson = Gson()
        val destinationType = object : TypeToken<List<Destination>>() {}.type
        val destinations: List<Destination> = gson.fromJson(destinationsJson, destinationType)

        dayWiseDestinations = distributeDestinationsEvenly(destinations, tripDuration)

        // 先加载第一天行程
        loadPlanForDay(1, dayWiseDestinations, travelMode)

        // 创建 Day Tabs
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
                    loadPlanForDay(i, dayWiseDestinations, travelMode)
                    highlightButton(this, daysLayout)
                }
            }
            if (i == 1) {
                defaultButton = button
            }
            daysLayout.addView(button)
        }

        // 高亮并加载第一天的行程
        defaultButton?.let {
            loadPlanForDay(1, dayWiseDestinations, travelMode)
            highlightButton(it, daysLayout)
        }

        // Initialize buttons
        val homeButton: Button = findViewById(R.id.home)
        val savedPlansButton: Button = findViewById(R.id.plan)
        val viewDetailsButton: Button = findViewById(R.id.view_details_button)

        // Home Page按钮
        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Saved Plans按钮（目前无操作）
        savedPlansButton.setOnClickListener {
            // Stay on the current page
        }

        // View Details按钮
        viewDetailsButton.setOnClickListener {
            val intent = Intent(this, DetailPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getDurationBetweenDestinations(start: Destination, end: Destination, mode: String): String {
        // TODO: 实现真实的路线查询逻辑调用MapService.getDirections
        return "10 min" // 暂时写死
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
                // 最后一个目的地，不需要duration
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

    private fun distributeDestinationsEvenly(destinations: List<Destination>, days: Int): List<List<Destination>> {
        if (destinations.isEmpty() || days <= 0) return listOf()
        val result = mutableListOf<List<Destination>>()
        val size = destinations.size
        val baseCount = size / days
        val remainder = size % days

        var startIndex = 0
        for (day in 1..days) {
            val countForThisDay = if (day <= remainder) baseCount + 1 else baseCount
            val dayDestinations = destinations.subList(startIndex, startIndex + countForThisDay)
            result.add(dayDestinations)
            startIndex += countForThisDay
        }
        return result
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