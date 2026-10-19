package com.example.offlinerouteguide.dataStructures

data class Route(
    val geometry: String,
    val legs: List<Legs>,
    val distance: Double,
    val duration: Double
)
