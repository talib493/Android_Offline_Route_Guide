package com.example.offlinerouteguide.MapLibre

import com.example.offlinerouteguide.dataStructures.bBoxResponse
import kotlin.math.cos

class maplibreRepo {

    fun calculateBbox(currentLongitude: Double, currentLatitude: Double ):bBoxResponse{
        val boundary = 20
        val kmRelativeToLatitudeDegree = 111.32
        val latitudeRadians = Math.toRadians(currentLatitude)
        val latitudeDelta = boundary/kmRelativeToLatitudeDegree
        val longitudeDelta = boundary/(kmRelativeToLatitudeDegree * cos(latitudeRadians))
        return bBoxResponse(
            north = currentLatitude+latitudeDelta,
            south = currentLatitude-latitudeDelta,
            west = currentLongitude-longitudeDelta,
            east = currentLongitude+longitudeDelta
        )
    }
}