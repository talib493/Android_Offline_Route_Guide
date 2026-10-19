package com.example.offlinerouteguide.dataStructures


data class GeoJson(
    val type: String = "FeatureCollection",
    val features:List<Feature>
)
