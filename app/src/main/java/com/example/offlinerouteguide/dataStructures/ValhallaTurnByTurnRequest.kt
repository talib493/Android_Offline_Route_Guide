package com.example.offlinerouteguide.dataStructures

data class ValhallaTurnByTurnRequest (
    val locations: List<ValhallaLocations>,
    val costing: String,
    val format : String = "osrm"
)