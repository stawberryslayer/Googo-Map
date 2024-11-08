package com.cs407.map_application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
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
        }

        // Initialize the map
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapService = MapService(googleMap)

        // Now you can use mapService to interact with the map
        val defaultLocation = LatLng(37.7749, -122.4194) // Example coordinates
        mapService.initializeMap(defaultLocation, 10f)
        mapService.addMarker(defaultLocation, "San Francisco")
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
