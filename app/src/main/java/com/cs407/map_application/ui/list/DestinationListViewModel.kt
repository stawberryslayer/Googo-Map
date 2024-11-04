package com.cs407.map_application.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.map_application.model.Destination

class DestinationListViewModel : ViewModel() {
    private val _destinations = MutableLiveData<List<Destination>>()
    val destinations: LiveData<List<Destination>> = _destinations

    private val _errorEvent = MutableLiveData<String>()
    val errorEvent: LiveData<String> = _errorEvent

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
}