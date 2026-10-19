package com.example.offlinerouteguide.dataStructures

data class Maneuver(
    val instruction: String,
    val type: String,
    val modifier: String?,
    val bearingAfter: Double?,
    val bearingBefore: Double?,
    val location: List<Double>
)
