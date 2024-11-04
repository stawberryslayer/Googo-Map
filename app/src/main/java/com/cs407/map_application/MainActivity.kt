package com.cs407.map_application

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.map_application.databinding.ActivityMainBinding
import com.cs407.map_application.model.Destination
import com.cs407.map_application.ui.list.DestinationAdapter
import com.cs407.map_application.ui.list.DestinationListViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DestinationListViewModel by viewModels()
    private lateinit var adapter: DestinationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.rvDestinations.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
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
        binding.fabAdd.setOnClickListener {
            // 添加测试数据
            val destination = Destination(
                name = "Test Location ${System.currentTimeMillis()}",
                address = "Test Address",
                latitude = 0.0,
                longitude = 0.0
            )
            viewModel.addDestination(destination)
        }
    }
}