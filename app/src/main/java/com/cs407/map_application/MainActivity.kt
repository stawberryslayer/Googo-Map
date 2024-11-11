package com.cs407.map_application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.GoogleMap

class MainActivity : AppCompatActivity() {
    private lateinit var mapService: MapService
    private lateinit var googleMap: GoogleMap




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mapService = MapService(applicationContext)
            
        // 找到 "Start Plan" 按钮（相当于home page）
        val startPlanButton: Button = findViewById(R.id.button_start_plan)
        // start button for making API request
        val startButton: Button = findViewById(R.id.button_start)

        // 设置按钮点击事件
        startPlanButton.setOnClickListener {
            val intent = Intent(this, DetailPage::class.java)
            startActivity(intent)
        }
        startButton.setOnClickListener{
            //send request
            val origin = "New York, NY"
            val destination = "Los Angeles, CA"

            // Call the map service to get directions
            mapService.getDirections(origin, destination, lifecycleScope) { success, message ->
                if (success) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }

        }

    }




    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
    }



}
