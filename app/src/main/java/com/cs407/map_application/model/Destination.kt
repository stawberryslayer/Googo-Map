package com.cs407.map_application.model

import java.util.UUID

data class Destination(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    var sequence: Int = 0
)