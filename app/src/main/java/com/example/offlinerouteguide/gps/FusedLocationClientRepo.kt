package com.example.offlinerouteguide.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.offlinerouteguide.dataStructures.getLocationStruct
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import org.maplibre.compose.location.BearingWithAccuracy
import org.maplibre.compose.location.Location
import org.maplibre.compose.location.PositionWithAccuracy
import org.maplibre.compose.location.SpeedWithAccuracy
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.units.Bearing
import org.maplibre.spatialk.units.extensions.degrees
import org.maplibre.spatialk.units.extensions.meters
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class FusedLocationClientRepo(
    val context: Context
) {
    val myLocationProvider = MyrLocationProvider()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val request = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000L
    )
        .setMinUpdateIntervalMillis(500L)
        .setMinUpdateDistanceMeters(5f)
        .build()

    fun getLocation(onSuccesss:(getLocationStruct)-> Unit){
        val callback = object:LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation?:return
                onSuccesss(
                    getLocationStruct(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = location.time
                    ))
                myLocationProvider.update(
                    location = Location(
                        timestamp = TimeSource.Monotonic.markNow(),
                        position = PositionWithAccuracy(
                            value = Position(
                                latitude = location.latitude,
                                longitude = location.longitude,
                            ),
                            accuracy = location.accuracy.toDouble().meters
                        ),
                        speed = SpeedWithAccuracy(
                            distancePerSecond = location.speed.toDouble().meters,
                            accuracy = location.accuracy.toDouble().meters

                        ),
                        // TODO: Populate course when MapLibre exposes a public Bearing factory
                        course = null
                    )

                )
            }
        }
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }else{
            fusedLocationClient.requestLocationUpdates(
                request,callback, Looper.getMainLooper()
            )

        }


    }
    fun checkLocationSettings(
        onSuccess: () -> Unit,
        onFailure: (ResolvableApiException) -> Unit
    ) {
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
            .build()

        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    onFailure(e)
                }
            }
    }
}