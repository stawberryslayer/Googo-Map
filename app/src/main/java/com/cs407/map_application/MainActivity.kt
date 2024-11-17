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
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.map_application.databinding.ActivityMainBinding
import com.cs407.map_application.model.Destination
import com.cs407.map_application.ui.list.DestinationAdapter
import com.cs407.map_application.ui.list.DestinationListViewModel

import com.google.android.gms.maps.GoogleMap

class MainActivity : AppCompatActivity() {
    private lateinit var mapService: MapService
    private lateinit var googleMap: GoogleMap
  
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DestinationListViewModel by viewModels()
    private lateinit var adapter: DestinationAdapter




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//         binding = ActivityMainBinding.inflate(layoutInflater)
//         setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = DestinationAdapter().apply {
            setOnItemClickListener { destination ->
                Toast.makeText(
                    this@MainActivity,
                    "Clicked: ${destination.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            setOnDeleteClickListener { destination ->
                viewModel.removeDestination(destination)
            }
        }

//        binding.rvDestinations.apply {
//            layoutManager = LinearLayoutManager(this@MainActivity)
//            adapter = this@MainActivity.adapter
//        }
    }

    private fun setupObservers() {
        viewModel.destinations.observe(this) { destinations ->
            adapter.setDestinations(destinations)
        }

        viewModel.errorEvent.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
//        binding.fabAdd.setOnClickListener {
//            // 添加测试数据
//            val destination = Destination(
//                name = "Test Location ${System.currentTimeMillis()}",
//                address = "Test Address",
//                latitude = 0.0,
//                longitude = 0.0
//            )
//            viewModel.addDestination(destination)
//        }
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


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

            val origin = "832 Regent St, Madison, WI 53715"
            val destination = "650 Elm Drive, Madison, WI 53706"

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
