package com.example.offlinerouteguide.valhallaRoutingEngine

import android.location.Location
import android.util.Log
import com.example.offlinerouteguide.dataStructures.GeoPoint
import com.example.offlinerouteguide.dataStructures.Maneuver
import com.example.offlinerouteguide.dataStructures.NextStep
import com.example.offlinerouteguide.dataStructures.Step
import com.example.offlinerouteguide.dataStructures.StepDistanceCalculate
import com.example.offlinerouteguide.dataStructures.ValhallaLocations
import org.maplibre.compose.expressions.dsl.step

class valhallaOnlineRepo {
    fun decodeValhallaPolyline(encoded: String): List<GeoPoint> {
        val polyline = ArrayList<GeoPoint>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        val precisionMultiplier = 1e6

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
                }
            while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
                }
            while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val finalLat = lat / precisionMultiplier
            val finalLng = lng / precisionMultiplier
            polyline.add(GeoPoint(finalLat, finalLng))
            }

            return polyline
        }

fun calculateNextStep(
    currentLocation: ValhallaLocations,
    steps: List<StepDistanceCalculate>,
    nextStep: Int,
    onSuccess:(Int) -> Unit
): NextStep {

    Log.d("NEXTSTEP","ViewModel Ns:"+nextStep.toString())

    var next = nextStep.coerceIn(1, steps.lastIndex)

    Log.d("NEXTSTEP","Ns:"+next.toString())

    val step = steps[next]
    Log.d("NEXTSTEP","Step"+step.toString())
    val startPoint = Location("start").apply {
        latitude = currentLocation.lat
        longitude = currentLocation.lon
    }
    val coordinate = step.firstCoordinate[0]

    val distance = FloatArray(1)

    Location.distanceBetween(
        currentLocation.lat,
        currentLocation.lon,
        coordinate.latitude,
        coordinate.longitude,
        distance
    )
    val stepDistance = distance[0]
    Log.d("NEXTSTEP","Distance:"+stepDistance.toString())


    // Within 50m and not yet reached → show upcoming step
    if (stepDistance <= 20 && stepDistance > 5) {
        onSuccess(next)
        Log.d("NEXTSTEP","upcoming condition true:")
        var endPoint = Location("end").apply {
            latitude = step.firstCoordinate[0].latitude
            longitude = step.firstCoordinate[0].longitude
           }
        val bearing = startPoint.bearingTo(endPoint)
        Log.d("NEXTSTEP","upcoming condition true bearing:"+ bearing.toString())
        return NextStep(
            instruction = step.maneuver.instruction,
            bearing = bearing.toDouble(),
            nextStep = next,
            maneuver = step.maneuver
        )
    }

    // Reached the step → advance to the next one
    if (stepDistance <= 5) {
        Log.d("NEXTSTEP","nextStep condition true:")
        next = (next + 1).coerceAtMost(steps.lastIndex)
        onSuccess(next)
        var endPoint = Location("end").apply {
            latitude = steps[next].firstCoordinate[0].latitude
            longitude = steps[next].firstCoordinate[0].longitude
        }
        val bearing = startPoint.bearingTo(endPoint)
        Log.d("NEXTSTEP","nextStep condition true:"+bearing.toString())

        return NextStep(
            instruction = steps[next].maneuver.instruction,
            bearing = bearing.toDouble(),
            nextStep = next,
            maneuver = steps[next].maneuver
        )
    }

    // More than 50m away → keep previous instruction
    val previousStep = steps[next - 1]
    Log.d("NEXTSTEP","step:"+previousStep.toString())
    onSuccess(next-1)

    var endPoint = Location("end").apply {
        latitude = previousStep.firstCoordinate[0].latitude
        longitude = previousStep.firstCoordinate[0].longitude
    }
    val bearing = startPoint.bearingTo(endPoint)
    Log.d("NEXTSTEP","Prevstep:"+bearing.toString())

    return NextStep(
        instruction = previousStep.maneuver.instruction,
        bearing = bearing.toDouble(),
        nextStep = next,
        maneuver = previousStep.maneuver
    )
}
    fun calculateBearing(
        currentLocation: GeoPoint,
        routeCordList: List<GeoPoint>
    ): Double {

        if (routeCordList.size < 2) return 0.0

        val distance = FloatArray(1)

        var closestIndex = 0
        var closestDist = Double.MAX_VALUE

        for (i in routeCordList.indices) {

            val routePoint = routeCordList[i]

            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                routePoint.latitude,
                routePoint.longitude,
                distance
            )

            if (distance[0] < closestDist) {
                closestDist = distance[0].toDouble()
                closestIndex = i
            }
        }

        val lookAheadIndex = minOf(
            closestIndex + 2,
            routeCordList.lastIndex
        )

        val startPoint = Location("start").apply {
            latitude = routeCordList[closestIndex].latitude
            longitude = routeCordList[closestIndex].longitude
        }

        val endPoint = Location("end").apply {
            latitude = routeCordList[lookAheadIndex].latitude
            longitude = routeCordList[lookAheadIndex].longitude
        }

        val bearing = startPoint.bearingTo(endPoint)

        Log.d("BEARINGG", "closestIndex = $closestIndex")
        Log.d("BEARINGG", "closestDist = $closestDist")
        Log.d("BEARINGG", "routeStart = ${routeCordList[closestIndex]}")
        Log.d("BEARINGG", "routeEnd = ${routeCordList[lookAheadIndex]}")
        Log.d("BEARINGG", "routeBearing = $bearing")

        return ((bearing + 360) % 360).toDouble()
    }

    fun calculateDistance(
        nextStep: Int,
        currentLocation: GeoPoint,
        steps:List<Step>
    ): Double {
        val distance = FloatArray(1)
//        var currentPoint = steps[nextStep].maneuver.location
//        if (nextStep == 0) {
//            Location.distanceBetween(
//                currentLocation.latitude,
//                currentLocation.longitude,
//                currentPoint[1],
//                currentPoint[0],
//                distance
//            )
//        }
//        else{
            val currentPoint = steps.last().maneuver.location
            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                currentPoint[1],
                currentPoint[0],
                distance
            )


        Log.d("DIST","cl:"+currentLocation +" nextStep"+nextStep+" step:"+ currentPoint + " dist:"+distance[0])
        return distance[0].toDouble()

    }


    fun calculatePolylineDistanceMeters(points: List<GeoPoint>): Double {
        if (points.size < 2) return 0.0
        var totalDistance = 0.0
        val results = FloatArray(1)

        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            Location.distanceBetween(
                p1.latitude, p1.longitude,
                p2.latitude, p2.longitude,
                results
            )
            totalDistance += results[0]
        }

        return totalDistance
    }

    fun getRemainingPolyline(currentLocation: GeoPoint, coordinates: List<GeoPoint> ): List<GeoPoint> {
        if (coordinates.isEmpty()) return emptyList()

        var closestIndex = 0
        var minDistance = Double.MAX_VALUE
        val dist = FloatArray(1)

        for (i in coordinates.indices) {
            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                coordinates[i].latitude,
                coordinates[i].longitude,
                dist
            )
            if (dist[0].toDouble() < minDistance) {
                minDistance = dist[0].toDouble()
                closestIndex = i
            }
        }

        val remainingList = coordinates.subList(closestIndex, coordinates.size).toMutableList()

        if (remainingList.isNotEmpty()) {
            remainingList[0] = currentLocation
        }

        return remainingList
    }
}