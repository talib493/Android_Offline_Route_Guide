package com.example.offlinerouteguide.models

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinerouteguide.Retrofit.RetrofitCient
import com.example.offlinerouteguide.Retrofit.RetrofitCient.valhallaApi
import com.example.offlinerouteguide.dataStructures.Feature
import com.example.offlinerouteguide.dataStructures.GeoJson
import com.example.offlinerouteguide.dataStructures.GeoPoint
import com.example.offlinerouteguide.dataStructures.Geometry
import com.example.offlinerouteguide.dataStructures.NextStep
import com.example.offlinerouteguide.dataStructures.PhotonFeature
import com.example.offlinerouteguide.dataStructures.PhotonGeometry
import com.example.offlinerouteguide.dataStructures.Step
import com.example.offlinerouteguide.dataStructures.StepDistanceCalculate
import com.example.offlinerouteguide.dataStructures.ValhallaLocations
import com.example.offlinerouteguide.dataStructures.ValhallaRequest
import com.example.offlinerouteguide.dataStructures.ValhallaTurnByTurnRequest
import com.example.offlinerouteguide.dataStructures.ValhallaTurnByTurnResponse
import com.example.offlinerouteguide.dataStructures.getLocationStruct
import com.example.offlinerouteguide.gps.FusedLocationClientRepo
import com.example.offlinerouteguide.valhallaRoutingEngine.valhallaOnlineRepo
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import java.io.File

class MainScreenViewModel(application: Application): AndroidViewModel(application) {

    var infoSheet by mutableStateOf(false)

    var isTripStarted by mutableStateOf(false)

    var isHideClicked by mutableStateOf(false)

    var StartselectedLocation by mutableStateOf("")
    var destSelectedLocation by mutableStateOf("")

    var startIsFocused by mutableStateOf(false)

    var  destIsFocused by mutableStateOf(false)

    var isDrawerOpen by mutableStateOf(false)


    var searchJob by mutableStateOf<Job?>(null)
    var destSearchJob by mutableStateOf<Job?>(null)


    var isDestClicked by mutableStateOf(false)

    var startName by mutableStateOf("")

    fun updateStartName(value: String){
        startName=value
    }

    var destName by mutableStateOf("")

    fun updateDestName(value: String){
        destName=value
    }
    var pointName by mutableStateOf("")

    var searchValue by mutableStateOf("")

    val _results = MutableStateFlow<List<PhotonFeature>>(emptyList())
    val results = _results.asStateFlow()

    val _decodedCord = MutableStateFlow<List<GeoPoint>>(emptyList())
    val decodedCord = _decodedCord.asStateFlow()

    val _steps = MutableStateFlow<List<Step>>(emptyList())
    val steps = _steps.asStateFlow()

    val dest_results = MutableStateFlow<List<PhotonFeature>>(emptyList())

    val destResults = dest_results.asStateFlow()

    val _tripSteps = MutableStateFlow<List<StepDistanceCalculate>>(emptyList())

    val tripSteps = _tripSteps.asStateFlow()

    var tripBearing by mutableStateOf(0.0)
    var instruction by mutableStateOf("")
    fun updateSearchValue(value: String){
        searchValue=value
    }
    val fusedLocationClientRepo = FusedLocationClientRepo(application)
    val locations = mutableStateListOf<getLocationStruct>()

    var userCoordinates = mutableStateListOf<List<Double>>(listOf(0.00,0.00))

    val currentLocation = listOf(ValhallaLocations(lat = 0.0 , lon = 0.0))

    val destCord = mutableStateListOf<ValhallaLocations>((ValhallaLocations(lat = 0.0 , lon = 0.0)))

    var duration by mutableStateOf("")
    var distance by mutableStateOf("")

    var isDrawerHidden by mutableStateOf(false)

    var distanceToPoint by mutableStateOf(0.0)


    var userMovement by mutableStateOf( GeoJson(
        type = "FeatureCollection",
        features = listOf(Feature(
            type = "Feature",
            geometry = Geometry(
                type = "LineString",
                coordinates = userCoordinates
            )
        )
        )
    ))

    var geoJ by mutableStateOf(
        GeoJson(
        type = "FeatureCollection",
        features = listOf(Feature(
            type = "Feature",
            geometry = Geometry(
                type = "LineString",
                coordinates = listOf()
            )
        )
        )
    )
      )
    var searchGeoJ by
        mutableStateOf(
            GeoJson(
                type = "FeatureCollection",
                features = listOf(
                    Feature(
                        type = "Feature",
                        geometry = PhotonGeometry(
                            type = "Point",
                            coordinates = emptyList()
                        )
                    )
                )
            )
        )
    var startrtGeoJson by
    mutableStateOf(
        GeoJson(
            type = "FeatureCollection",
            features = listOf(
                Feature(
                    type = "Feature",
                    geometry = PhotonGeometry(
                        type = "Point",
                        coordinates = emptyList()
                    )
                )
            )
        )
    )

    var timeChipData by mutableStateOf<GeoJsonData>(
        GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[]}""")
    )
    var distanceChipData by mutableStateOf<GeoJsonData>(
        GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[]}""")
    )
    var isUserInteracting by mutableStateOf(false)

    var nextStep = 0
    val valhallaOnlineRepo = valhallaOnlineRepo()





    fun decodeShape(shape: String):List<GeoPoint>{
        val result =valhallaOnlineRepo.decodeValhallaPolyline(shape)
        return result
    }

    fun checkGpsSetting(
        onResolutionRequired: (ResolvableApiException) -> Unit
    ){
        fusedLocationClientRepo.checkLocationSettings(
            onSuccess = {
                getContinuousLocation()
            },
            onFailure = {exception->
                onResolutionRequired(
                    exception
                )

            }
        )
    }

    fun updateTimeChip(longitude: Double, latitude: Double, time: String , dist: String) {
        duration = time
        distance = dist
        timeChipData = GeoJsonData.JsonString("""
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": [$longitude, $latitude]
              },
              "properties": {
           
              }
            }
          ]
        }
    """.trimIndent())
    }
    fun eraseTimeChip() {
        duration = ""
        timeChipData = GeoJsonData.JsonString("""
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": []
              },
              "properties": {
           
              }
            }
          ]
        }
    """.trimIndent())
    }
    fun updateDistChip(longitude: Double, latitude: Double, time: String , dist: String) {
        distance = dist
        distanceChipData = GeoJsonData.JsonString("""
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": [$longitude, $latitude]
              },
              "properties": {
           
              }
            }
          ]
        }
    """.trimIndent())
    }

    fun eraseDistChip() {
        distance = ""
        distanceChipData = GeoJsonData.JsonString("""
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": []
              },
              "properties": {
           
              }
            }
          ]
        }
    """.trimIndent())
    }

    fun getContinuousLocation(){
        fusedLocationClientRepo.getLocation { location ->
            locations.add(location)
            if (userCoordinates[0]==listOf(0.00,0.00)){
                userCoordinates.clear()
                userCoordinates.add(listOf(location.longitude,location.latitude))
            }
            else{
                userCoordinates.add(listOf(location.longitude,location.latitude))
            }
        }
    }


    fun getTurnByTurnInstructions(request: ValhallaTurnByTurnRequest , onSuccess: (ValhallaTurnByTurnResponse) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
        try{
            val response = RetrofitCient.stadiaApi.getStadiaTurnByTurnRoute(request)
            if (response.isSuccessful){
                onSuccess(response.body()!!)
                Log.d("VALHALLA",response.body().toString())
                val decoded = decodeShape(response.body()!!.routes[0].geometry)
                _decodedCord.value = decoded
                val step = response.body()!!.routes[0].legs[0].steps
                _steps.value = step

                val result = step.map {step->
                    StepDistanceCalculate(
                        firstCoordinate = listOf(GeoPoint(
                            latitude = decodeShape(step.geometry)[0].latitude,
                            longitude = decodeShape(step.geometry)[0].longitude
                        )),
                        maneuver = step.maneuver
                    )
                }
                _tripSteps.value = result
                isDrawerOpen = true
            }
            else{
                Log.e(
                    "VALHALLA",
                    "HTTP ${response.code()}: ${response.errorBody()?.string()}"
                )
            }
        }catch (e : Exception){
            Log.e("VALHALLA", "Request failed", e)
        }
        }

    }

    fun searchOnline(search: String){
    destSearchJob?.cancel()
    searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
           val response =  RetrofitCient.apiService.search(search)
            _results.value = response.features
        }catch (e: Exception){
                Log.e("VALHALLA", "Request failed", e)

        }
        }
    }
    fun destSearchOnline(search: String){
        searchJob?.cancel()
        destSearchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitCient.apiService.search(search)
                dest_results.value = response.features
                Log.d("DEST", "dest request${destResults.value}")

            } catch (e: Exception) {
                Log.e("DEST", "Request failed", e)

            }
        }
    }
    fun reverseSearch(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = RetrofitCient.apiService.reverseSearch(lon = lon , lat = lat)
                StartselectedLocation = response.features[0].properties.name.toString()
            }catch (e: Exception){
                Log.e("VALHALLA", "Request failed", e)
            }
        }
    }
    fun destReverseSearch(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = RetrofitCient.apiService.reverseSearch(lon = lon , lat = lat)
                destSelectedLocation = response.features[0].properties.name.toString()
            }catch (e: Exception){
                Log.e("VALHALLA", "Request failed", e)
            }
        }
    }

    fun clear(){
        searchValue=""
        _results.value = emptyList()
    }

    fun startCircleLayerRender(coordinates: List<Double>){
        startrtGeoJson = startrtGeoJson.copy(
            features = listOf(
                Feature(
                    type = "Feature",
                    geometry = PhotonGeometry(
                        type = "Point",
                        coordinates = coordinates
                    )
                )
            )
        )
    }

    fun startCircleLayerEraser(){
        startrtGeoJson = startrtGeoJson.copy(
            features = listOf(
                Feature(
                    type = "Feature",
                    geometry = PhotonGeometry(
                        type = "Point",
                        coordinates = emptyList()
                    )
                )
            )
        )
    }

    fun circleLayerRender(coordinates: List<Double>){
        searchGeoJ = searchGeoJ.copy(
            features = listOf(
                Feature(
                    type = "Feature",
                    geometry = PhotonGeometry(
                        type = "Point",
                        coordinates = coordinates
                    )
                )
            )
        )
    }
    fun circleLayerEraser(){
        searchGeoJ = searchGeoJ.copy(
            features = listOf(
                Feature(
                    type = "Feature",
                    geometry = PhotonGeometry(
                        type = "Point",
                        coordinates = emptyList()
                    )
                )
            )
        )
    }
    fun lineLayerRender(coordinates:List<GeoPoint>){
        Log.d("VALHALLA","LineLayerRender CALLED")
        val mappedCoords = coordinates.map { listOf(it.longitude, it.latitude) }
        geoJ = GeoJson(
            type = "FeatureCollection",
            features = listOf(
                Feature(
                    type = "Feature",
                    geometry = Geometry(
                        type = "LineString",
                        coordinates = mappedCoords
                    )
                )
            )
        )
    }

    fun lineLayerEraser(){
        geoJ = GeoJson(
            type = "FeatureCollection",
            features = listOf(
                Feature(
                    type = "Feature",
                    geometry = Geometry(
                        type = "LineString",
                        coordinates = listOf(listOf(0.00,0.00))
                    )
                )
            )
        )
    }
    fun copyPmtiles(context: Context): File {
        val mapsDir = File(context.filesDir, "maps")
        mapsDir.mkdirs()

        val file = File(mapsDir, "ayodhya.pmtiles")

        if (!file.exists()) {
            context.assets.open("maps/ayodhya.pmtiles").use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        return file
    }
    fun pointName(name: String){
        pointName = name
    }


    fun secToTimeDisplay(sec: Int): String {
        val hour = sec / 3600
        var min = (sec % 3600) / 60
        val remainingSec = sec % 60

        if (hour > 0) {
            return "${hour}hr ${min}m ${remainingSec}s"
        }
        return "${min}min ${remainingSec}s"
    }
    fun formatDistance(meters: Int): String {
        if (meters >= 1000) {
            val km = meters / 1000.0
            return String.format("%.1f km", km)
        }
        return "${meters} m"
    }

    fun calculateNextStep(currentLocation: ValhallaLocations , steps:List<StepDistanceCalculate>){
        val response = valhallaOnlineRepo.calculateNextStep(currentLocation , steps , nextStep , onSuccess = {nextStep->
            calculateDistanceLeft(currentLocation = GeoPoint(
                latitude = currentLocation.lat,
                longitude = currentLocation.lon
            ))
        })
        Log.d("TRIP","RESPONSE"+response.toString())
        nextStep = response.nextStep
        instruction = response.instruction
    }
    fun calculateBearing (currentLocation: GeoPoint, geometryCords:List<GeoPoint>){
        val bearing = valhallaOnlineRepo.calculateBearing(currentLocation,geometryCords)
        tripBearing  = bearing

    }
    fun calculateDistance(currentLocation: GeoPoint,steps:List<Step>){
        val response = valhallaOnlineRepo.calculateDistance(nextStep ,currentLocation, steps)
    }

    fun calculateDistanceLeft(currentLocation: GeoPoint ){
        val distList = valhallaOnlineRepo.getRemainingPolyline(currentLocation , _decodedCord.value)
        val response = valhallaOnlineRepo.calculatePolylineDistanceMeters(points = distList)
        distanceToPoint = response
    }


}