package com.example.offlinerouteguide.dataStructures

data class Step(
    val geometry: String,
    val distance: Double,
    val duration: Double,
    val maneuver: Maneuver
)

