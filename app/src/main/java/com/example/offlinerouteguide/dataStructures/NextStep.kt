package com.example.offlinerouteguide.dataStructures

import org.maplibre.spatialk.units.Bearing

data class NextStep(
    val maneuver: Maneuver,
    val instruction: String = "",
    val bearing: Double?,
    val nextStep: Int
)
