package com.cs407.map_application.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cs407.map_application.model.Destination
import com.cs407.map_application.model.TripPlanner
import kotlinx.coroutines.launch

class DestinationListViewModel(application: Application) : AndroidViewModel(application) {
    private val tripPlanner = TripPlanner(application)
    private val _destinations = MutableLiveData<List<Destination>>()
    val destinations: LiveData<List<Destination>> = _destinations

    private val _errorEvent = MutableLiveData<String>()
    val errorEvent: LiveData<String> = _errorEvent

    private val _tripPlans = MutableLiveData<List<TripPlanner.DailyPlan>>()
    val tripPlans: LiveData<List<TripPlanner.DailyPlan>> = _tripPlans

    init {
        _destinations.value = emptyList()
    }

    fun addDestination(destination: Destination) {
        val currentList = _destinations.value?.toMutableList() ?: mutableListOf()
        destination.sequence = currentList.size
        currentList.add(destination)
        _destinations.value = currentList
    }

    fun removeDestination(destination: Destination) {
        val currentList = _destinations.value?.toMutableList() ?: mutableListOf()
        currentList.remove(destination)
        // 更新序列号
        currentList.forEachIndexed { index, dest ->
            dest.sequence = index
        }
        _destinations.value = currentList
    }

    fun createTripPlan(numberOfDays: Int) {
        viewModelScope.launch {
            try {
                val locationIds = _destinations.value?.map { it.id } ?: emptyList()
                if (locationIds.isEmpty()) {
                    _errorEvent.value = "请先添加目的地"
                    return@launch
                }
                val plans = tripPlanner.createTripPlan(locationIds, numberOfDays)
                _tripPlans.value = plans
            } catch (e: Exception) {
                _errorEvent.value = "创建行程计划失败: ${e.message}"
            }
        }
    }
}