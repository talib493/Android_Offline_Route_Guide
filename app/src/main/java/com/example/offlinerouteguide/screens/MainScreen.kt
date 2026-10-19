package com.example.offlinerouteguide.screens


import android.Manifest
import android.app.Activity
import android.graphics.Camera
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.offlinerouteguide.R
import com.example.offlinerouteguide.dataStructures.GeoPoint
import com.example.offlinerouteguide.dataStructures.ValhallaLocations
import com.example.offlinerouteguide.dataStructures.ValhallaRequest
import com.example.offlinerouteguide.dataStructures.ValhallaTurnByTurnRequest
import com.example.offlinerouteguide.models.MainScreenViewModel
import com.example.offlinerouteguide.ui_components.InfoBottomSheet
import com.example.offlinerouteguide.ui_components.InstructionCard
import com.example.offlinerouteguide.ui_components.MainScreenDirectionSearchBar
import com.example.offlinerouteguide.ui_components.MainScreenSearchBar
import com.example.offlinerouteguide.ui_components.SearchResultScrollable
import com.example.offlinerouteguide.ui_components.currentLocationChip
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.style.expressions.Expression.concat
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.get
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.offset
import org.maplibre.compose.expressions.dsl.padding
import org.maplibre.compose.expressions.dsl.plus
import org.maplibre.compose.expressions.value.DpValue
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.expressions.value.TextUnitOffsetValue
import org.maplibre.compose.expressions.value.TextUnitValue
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.location.LocationPuck
import org.maplibre.compose.location.LocationPuckColors
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.mostAccurateBearing
import org.maplibre.compose.location.rememberDefaultOrientationProvider
import org.maplibre.compose.location.rememberUserLocationState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentlocation = viewModel.locations.lastOrNull()

    var initialCameraMoved by remember { mutableStateOf(false) }

    val camera = rememberCameraState()

    val locationProvider = remember { viewModel.fusedLocationClientRepo.myLocationProvider }

    val orientationProvider = rememberDefaultOrientationProvider()

    val locationState = rememberUserLocationState(locationProvider, orientationProvider)

    val searchList = viewModel.results.collectAsState()

    val destSearchList = viewModel.destResults.collectAsState()

    val tripList = viewModel.tripSteps.collectAsState()

    val geometryCordsList = viewModel.decodedCord.collectAsState()

    val steps = viewModel.steps.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


    LaunchedEffect(viewModel.isDrawerOpen) {
        if (viewModel.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    "https://api.protomaps.com/tiles/v4/{z}/{x}/{y}.mvt?key=ca5ab4676389e7ef"
//LaunchedEffect(Unit) {
//    scope.launch(Dispatchers.IO) {
//        val pmtilesUrl = URL("https://protomaps.com")
//
//        try {
//            HttpUrlConnectionChannel(pmtilesUrl).use { channel ->
//                // Initialize the actual PMTilesReader using the network channel
//                val reader = Reader(channel)
//
//                // Now you can safely request standard ZXY tiles out of that archive block
//                val tile = reader.getTile(12, 1, 1)
//
//                if (tile != null) {
//                    Log.d("PMTILE", "Success! Tile byte size: ${tile.size}")
//                } else {
//                    Log.d("PMTILE", "Tile does not exist in this archive dataset.")
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("PMTILE", "Error reading PMTiles archive: ${e.message}", e)
//        }
//    }
//}


    val pmtiles = viewModel.copyPmtiles(context)
//    Log.d("PMTILES", pmtiles.absolutePath)
//    Log.d("PMTILES", "exists = ${pmtiles.exists()}")
//    Log.d("PMTILES", "size = ${pmtiles.length()}")

    val styleJson = context.assets
        .open("maps/ayodhya-style.json")
        .bufferedReader()
        .use { it.readText() }


    val bounds = BoundingBox(
        west = 82.05,  // Minimum Longitude (Southwest corner X)
        south = 26.72, // Minimum Latitude  (Southwest corner Y)
        east = 82.25,  // Maximum Longitude (Northeast corner X)
        north = 26.90  // Maximum Latitude  (Northeast corner Y)
    )

    LaunchedEffect(viewModel.isTripStarted) {
        while (viewModel.isTripStarted) {
            Log.d(
                "TRIP",
                currentlocation?.longitude.toString() + viewModel.isTripStarted + tripList.value
            )
            if (currentlocation != null && viewModel.isTripStarted && tripList.value.isNotEmpty() && geometryCordsList.value.isNotEmpty()) {
                viewModel.calculateNextStep(
                    currentLocation = ValhallaLocations(
                        lat = currentlocation.latitude,
                        lon = currentlocation.longitude
                    ),
                    steps = tripList.value
                )
                viewModel.calculateBearing(
                    currentLocation = GeoPoint(
                        latitude = currentlocation.latitude,
                        longitude = currentlocation.longitude
                    ),
                    geometryCords = geometryCordsList.value
                )
                viewModel.calculateDistance(
                    currentLocation = GeoPoint(
                        latitude = currentlocation.latitude,
                        longitude = currentlocation.longitude
                    ),
                    steps = steps.value
                )
            }
            delay(2000L)
        }
    }


    LaunchedEffect(viewModel.searchValue) {
        viewModel.searchJob?.cancel()
        delay(400)
        if (viewModel.searchValue.length > 2) {
            viewModel.searchOnline(viewModel.searchValue)
        }
        if (viewModel.startName.isBlank() || viewModel.searchValue.isBlank()) {
            viewModel._results.value = emptyList()
        }
    }
    LaunchedEffect(viewModel.startName) {
        delay(400)

        if (viewModel.startName.length > 2) {
            viewModel.searchOnline(viewModel.startName)
        }

        if (viewModel.startName.isBlank()) {
            viewModel._results.value = emptyList()
        }
    }
    LaunchedEffect(viewModel.destName) {
        delay(400)
        if (viewModel.destName.length > 2) {
            viewModel.destSearchOnline(viewModel.destName)
        }
        if (viewModel.destName.isBlank()) {
            viewModel.dest_results.value = emptyList()
        }
    }


    LaunchedEffect(initialCameraMoved, currentlocation) {
        Log.d("MAP", "LaunchedEffect: $currentlocation")
        if (currentlocation != null && !initialCameraMoved) {
            initialCameraMoved = true
            Log.d("MAP", "currentLocation != null: $currentlocation")
            camera.animateTo(
                finalPosition = camera.position.copy(
                    target = Position(
                        latitude = currentlocation.latitude,
                        longitude = currentlocation.longitude
                    ), zoom = 13.0
                ),
                duration = 3.seconds
            )
            Log.d("MAP", "Camera animate")
            Log.d("MAP", "Camera position: ${camera.position}")

        }
    }

    val gpsSettingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getContinuousLocation()
        } else {
            Toast.makeText(context, "Device Location Is Off", Toast.LENGTH_SHORT).show()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.checkGpsSetting { exception ->
                val request = IntentSenderRequest.Builder(
                    exception.resolution
                ).build()
                gpsSettingLauncher.launch(request)
            }
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        launcher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    val json by remember {
        derivedStateOf { Gson().toJson(viewModel.geoJ) }
    }

    val userJson by remember {
        derivedStateOf { Gson().toJson(viewModel.userMovement) }
    }

    val jsonSearched by remember { derivedStateOf { Gson().toJson(viewModel.searchGeoJ) } }

    val startJson by remember { derivedStateOf { Gson().toJson(viewModel.startrtGeoJson) } }




    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            viewModel.isUserInteracting = true
                            Log.d("DEBUG: ", "User touched the map")
                            Log.d("DEBUG: ", viewModel.isUserInteracting.toString())
                        }
                    }
                    false
                }
        ) {
            MaplibreMap(
                baseStyle = BaseStyle.Uri("https://api.protomaps.com/styles/v5/light/en.json?key=ca5ab4676389e7ef"),
                cameraState = camera,
                onMapClick = { pos, offset ->
                    val searchFeatures = camera.projection?.queryRenderedFeatures(
                        offset,
                        layerIds = setOf("user-search")
                    )

                    if (!searchFeatures.isNullOrEmpty()) {
                        Log.d("CLICKED", "Search marker clicked")
                        viewModel.circleLayerEraser()
                        return@MaplibreMap ClickResult.Consume
                    }

                    val features = camera.projection?.queryRenderedFeatures(offset)
                    val pointFeature = features?.firstOrNull { it.geometry is Point }

                    if (pointFeature != null) {
                        Log.d("FEATURE", "POI clicked: $pointFeature")
                        viewModel.pointName(
                            name = pointFeature.properties!!.get("name").toString().trim('"')
                        )
                        viewModel.destCord.clear()
                        viewModel.destCord.add(
                            ValhallaLocations(
                                lat = pos.latitude,
                                lon = pos.longitude
                            )
                        )
                        viewModel.destSelectedLocation = viewModel.pointName
                        viewModel.infoSheet = true
                        Log.d("VALHALLA", viewModel.locations.lastOrNull().toString())
                    }

                    ClickResult.Consume
                },
                onMapLongClick = { pos, offset ->
                    val features = camera.projection?.queryRenderedFeatures(offset)
                    if (!features.isNullOrEmpty()) {
                        viewModel.circleLayerRender(listOf(pos.longitude, pos.latitude))
                        Log.d("LONGCLICK", features.toString())
                        var name = features[0].properties?.get("name").toString().trim('"')
                        if (name == "null") {
                            name = "Unknown Location"
                        }
                        viewModel.pointName(name = name)
                        viewModel.destCord.clear()
                        viewModel.destCord.add(
                            ValhallaLocations(
                                lat = pos.latitude,
                                lon = pos.longitude
                            )
                        )
                        viewModel.destSelectedLocation = viewModel.pointName

                        viewModel.infoSheet = true
                        ClickResult.Consume
                    } else {
                        ClickResult.Pass
                    }
                },
                options = MapOptions(
                    ornamentOptions = OrnamentOptions(
                        padding = PaddingValues(0.dp),
                        isLogoEnabled = false,
                        logoAlignment = Alignment.BottomStart,
                        isAttributionEnabled = false,
                        attributionAlignment = Alignment.BottomEnd,
                        isCompassEnabled = true,
                        compassAlignment = Alignment.BottomStart,
                        isScaleBarEnabled = true,
                        scaleBarAlignment = Alignment.BottomEnd,
                    )
                )
            ) {
                Log.d("ROUTE", "Rendering source with JSON: $json")
                key(viewModel.timeChipData) {
                    val chipDataSource = rememberGeoJsonSource(
                        data  = viewModel.timeChipData
                    )

                    SymbolLayer(
                        id = "time-text",
                        source = chipDataSource,

                        textField = const(viewModel.duration),

                        textFont = const(
                            listOf("Noto Sans Regular")
                        ),
                        textSize = const(15.sp),
                        textColor = const(Color.Red),

                        textAllowOverlap = const(false),
                        textIgnorePlacement = const(true),
                        textAnchor = const(SymbolAnchor.Right),
                        )
                }
                key(viewModel.distanceChipData) {
                    val distDataSource = rememberGeoJsonSource(
                        data  = viewModel.distanceChipData
                    )
                    SymbolLayer(
                        id = "dist-text",
                        source = distDataSource,

                        textField = const(viewModel.distance),

                        textFont = const(
                            listOf("Noto Sans Regular")
                        ),
                        textSize = const(15.sp),
                        textColor = const(Color.Red),

                        textAllowOverlap = const(false),
                        textIgnorePlacement = const(true),
                        textAnchor = const(SymbolAnchor.Right),
                    )
                }
                key(json) {
                    val DestRouteSource = rememberGeoJsonSource(
                        data = GeoJsonData.JsonString(json = json)
                    )
                        LineLayer(
                            id = "dest-route",
                            source = DestRouteSource,
                            width = const(6.dp),
                            color = const(Color.Blue.copy(0.6f))
                        )

                }
                key(userJson) {
                    val userRouteSource = rememberGeoJsonSource(
                        data = GeoJsonData.JsonString(json = userJson)
                    )
                    LineLayer(
                        id = "user-route",
                        source = userRouteSource,
                        width = const(6.dp),
                        color = const(Color.Green.copy(0.6f))
                    )
                }
                key(jsonSearched) {
                    val searchedLocation =
                        rememberGeoJsonSource(data = GeoJsonData.JsonString(json = jsonSearched))

                    CircleLayer(
                        id = "user-search",
                        source = searchedLocation,
                        color = const(Color.White),
                        strokeColor = const(Color.Red),
                        strokeWidth = const(8.dp),
                        onClick = { features ->
                            ClickResult.Consume
                        }
                    )
                }
                key(startJson) {
                    val startCircleLocation =
                        rememberGeoJsonSource(data = GeoJsonData.JsonString(json = startJson))

                    CircleLayer(
                        id = "start-circle",
                        source = startCircleLocation,
                        color = const(Color.DarkGray),
                        strokeColor = const(Color.Gray),
                        strokeWidth = const(6.dp),
                        onClick = { features ->
                            ClickResult.Consume
                        }
                    )
                }
                LocationPuck(
                    colors = LocationPuckColors(
                        dotFillColorCurrentLocation = Color.Green.copy(0.5f),
                        dotStrokeColor = Color.Green,
                        dotFillColorOldLocation = Color.Gray,
                        bearingColor = Color.Red,
                        accuracyFillColor = Color.Green.copy(0.2f)
                    ),
                    idPrefix = "user",
                    location = locationState.location,
                    bearing = locationState.mostAccurateBearing(),
                    cameraState = camera,
                )
                if (!viewModel.isUserInteracting&&!viewModel.isTripStarted) {
                    LocationTrackingEffect(locationState = locationState) {
                        val position = locationState.location?.position?.value
                        if (position != null) {
                            camera.animateTo(
                                CameraPosition(
                                    target = Position(
                                        latitude = position.latitude,
                                        longitude = position.longitude,
                                        altitude = position.altitude
                                    ), zoom = 15.0
                                )
                            )
                        }
                        Log.d("DEBUG: ", "MapLibre Position : " + position)
                    }
                }
                if (viewModel.isTripStarted){
                    LocationTrackingEffect(locationState = locationState) {
                        val position = locationState.location?.position?.value
                        if (position != null) {
                            camera.animateTo(
                                CameraPosition(
                                    target = Position(
                                        latitude = position.latitude,
                                        longitude = position.longitude,
                                        altitude = position.altitude
                                    ), zoom = 18.0,
                                    tilt = 50.0,
                                    bearing = viewModel.tripBearing
                                )
                            )
                        }
                        Log.d("DEBUG: ", " Nav MapLibre Position : " + position)
                    }
                }
            }
        }
        if (viewModel.isTripStarted){
            Column(
                modifier = Modifier.statusBarsPadding()
            ) {
                InstructionCard(
                    instruction = viewModel.instruction,
                    distance = viewModel.formatDistance(viewModel.distanceToPoint.toInt())
                )
            }
        }
        if (viewModel.isDrawerHidden){
            FloatingActionButton(
                    contentColor = Color.Black,
                    containerColor = Color.White,
                onClick = {
                    viewModel.isDrawerHidden = false
                    viewModel.isDrawerOpen = true
                          },
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(bottom = 120.dp).height(100.dp).width(50.dp)) {
                Icon(
                    painter = painterResource(R.drawable.keyboard_double_arrow_right_24px),
                    contentDescription = "right arrow"
                )
            }
        }

        DismissibleNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    DismissibleDrawerSheet(
                        drawerContainerColor = Color.White,
                        drawerContentColor = Color.Black,
                        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight(0.5f).padding(bottom = 70.dp)
                    ) {
                       Column(modifier = Modifier.fillMaxSize()) {
                           IconButton(onClick = {
                               viewModel.isDrawerOpen = false
                               viewModel.isDrawerHidden = true
                           },
                               modifier = Modifier.align(Alignment.End)
                           ) {
                               Icon(
                                   painter = painterResource(R.drawable.visibility_off_24px),
                                   contentDescription = "hide drawer"
                               )
                           }
                           Column(modifier = Modifier.padding(8.dp)) {
                               FloatingActionButton (
                                   containerColor = Color.Black,
                                   contentColor = Color.White ,
                                   onClick = {
                                       viewModel.isTripStarted = true
                                       viewModel.isDrawerOpen = false
                                       viewModel.eraseTimeChip()
                                       viewModel.eraseDistChip()
                                   }) {
                                   Column (modifier = Modifier) {
                                       Icon(
                                           modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                                           painter = painterResource(R.drawable.directions_run_24px),
                                           contentDescription = "start trip"
                                       )
                                       Text(modifier = Modifier.padding(8.dp), text = "Start")
                                   }
                               }
                               Spacer(modifier = Modifier.height(8.dp))
                               FloatingActionButton (
                                   containerColor = Color.Black,
                                   contentColor = Color.White ,
                                   onClick = {
                                       Toast.makeText(context,"Coming Soon", Toast.LENGTH_SHORT).show()
                                   }) {
                                   Column (modifier = Modifier) {
                                       Icon(
                                           modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                                           painter = painterResource(R.drawable.pip_24px),
                                           contentDescription = "minimize trip"
                                       )
                                       Text(modifier = Modifier.padding(8.dp), text = "PIP")
                                   }
                               }
                               Spacer(modifier = Modifier.height(8.dp))
                               FloatingActionButton (
                                   containerColor = Color.Black,
                                   contentColor = Color.White ,
                                   onClick = {
                                       Toast.makeText(context,"Coming Soon", Toast.LENGTH_SHORT).show()
                                   }) {
                                   Column (modifier = Modifier) {
                                       Icon(
                                           modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                                           painter = painterResource(R.drawable.share_24px),
                                           contentDescription = "share trip"
                                       )
                                       Text(modifier = Modifier.padding(8.dp), text = "Share")
                                   }
                               }

                           }


                       }
                    }
                }
            }
        ) {}

        var padding = 150
        if (!viewModel.isUserInteracting && !viewModel.isDestClicked) {
            padding = 30
        }
        if(!viewModel.isUserInteracting && viewModel.isDestClicked){
            padding = 30
        }
        if (viewModel.isUserInteracting && !viewModel.isDestClicked&& !viewModel.isHideClicked) {
            padding = 90
        }
        if (!viewModel.isTripStarted) {
            FloatingActionButton(
                containerColor = Color.Black,
                onClick = {
                    viewModel.userCoordinates.clear()
                    viewModel.userCoordinates.add(0, listOf(0.0, 0.0))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = padding.dp, end = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 10.dp,
                            end = 5.dp,
                            bottom = 10.dp
                        ),
                        painter = painterResource(R.drawable.directions_walk_24px),
                        contentDescription = "location",
                        tint = Color.White
                    )
                    Text(
                        "Reset",
                        Modifier.padding(start = 5.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                        color = Color.White
                    )
                }
            }
        }

        if (viewModel.isUserInteracting&&!viewModel.isTripStarted) {
            FloatingActionButton(
                containerColor = Color.Black,
                onClick = { viewModel.isUserInteracting = false },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 30.dp, end = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 10.dp,
                            end = 5.dp,
                            bottom = 10.dp
                        ),
                        painter = painterResource(R.drawable.location_on_24px),
                        contentDescription = "location",
                        tint = Color.White
                    )
                    Text(
                        "Re-center",
                        Modifier.padding(start = 5.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                        color = Color.White
                    )
                }
            }
        }
        if (viewModel.isTripStarted) {
            FloatingActionButton(
                containerColor = Color.Black,
                onClick = {
                    viewModel.isTripStarted = false
                    viewModel.nextStep = 0
                    if(currentlocation!=null) {
                        scope.launch {
                            camera.animateTo(
                                finalPosition = camera.position.copy(
                                    target = Position(
                                        latitude = currentlocation.latitude,
                                        longitude = currentlocation.longitude
                                    ),
                                    zoom = 16.0,
                                    tilt = 0.0
                                ),
                                duration = 3.seconds
                            )
                        }
                    }
                          },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 30.dp, end = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(R.drawable.close_24px),
                        contentDescription = "Stop Trip",
                        tint = Color.White
                    )
                }
            }
        }

        if (viewModel.isHideClicked&&!viewModel.isTripStarted) {
            FloatingActionButton(
                containerColor = Color.Black,
                onClick = {
                    viewModel.isDestClicked = true
                    viewModel.isHideClicked = false
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 90.dp, end = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 10.dp,
                            end = 5.dp,
                            bottom = 10.dp
                        ),
                        painter = painterResource(R.drawable.visibility_24px),
                        contentDescription = "location",
                        tint = Color.White
                    )
                    Text(
                        "Route",
                        Modifier.padding(
                            start = 5.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ),
                        color = Color.White
                    )
                }
            }
        }


        // 2. SEARCH UI LAYER (Exclusive conditional rendering)
        if (viewModel.isDestClicked&&!viewModel.isTripStarted) {
            // Mode A: Direction Search Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .align(Alignment.TopCenter)
            ) {
                MainScreenDirectionSearchBar(
                    padding = 10,
                    startValue = viewModel.startName,
                    destValue = viewModel.destName,
                    startPlaceholderValue = viewModel.StartselectedLocation,
                    destPlaceholderValue = viewModel.destSelectedLocation,
                    onSearchClick = {
                        if (viewModel.locations != null) {
                            viewModel.isDestClicked = true
                            Log.d(
                                "VALHALLA",
                                "Current: ${viewModel.locations.last().latitude}, ${viewModel.locations.last().longitude}"
                            )
                            Log.d("VALHALLA", "Dest: ${viewModel.destCord}")

                            var startLat = viewModel.currentLocation[0].lat
                            var startLon = viewModel.currentLocation[0].lon
                            val currentLat = viewModel.locations.last().latitude
                            val currentLon = viewModel.locations.last().longitude
                            if (startLon == 0.0 && startLat == 0.0) {
                                startLon = currentLon
                                startLat = currentLat
                            }
                            viewModel.reverseSearch(
                                lat = startLat,
                                lon = startLon
                            )
                            val request = ValhallaTurnByTurnRequest(
                                locations = listOf(
                                    ValhallaLocations(
                                        lat = startLat,
                                        lon = startLon
                                    ),
                                    ValhallaLocations(
                                        lat = viewModel.destCord[0].lat,
                                        lon = viewModel.destCord[0].lon
                                    )
                                ),
                                costing = "auto"
                            )
                            viewModel.getTurnByTurnInstructions(request, onSuccess = { response ->
                                val shape = response.routes[0].geometry
                                val result = viewModel.decodeShape(shape)
                                if (result != null) {
                                    val lastStep = response.routes[0].legs[0]
                                    val totalDuration = lastStep.duration

                                    val lastStepLocation: List<Double> = lastStep.steps[lastStep.steps.lastIndex-1].maneuver.location
                                    val time = viewModel.secToTimeDisplay(totalDuration.toInt())
                                    val distance = viewModel.formatDistance(lastStep.distance.toInt())



                                    viewModel.updateTimeChip(longitude = lastStepLocation[0] , latitude = lastStepLocation[1], time = time,  dist = lastStep.distance.toString())
                                    viewModel.updateDistChip(longitude = lastStep.steps[1].maneuver.location[0] , latitude = lastStep.steps[1].maneuver.location[1], time = time,  dist = distance)


                                    Log.d("VALHALLA", "Result: ${result}")
                                    viewModel.lineLayerRender(result)
                                    viewModel.circleLayerRender(
                                        listOf(
                                            result[result.lastIndex].longitude,
                                            result[result.lastIndex].latitude
                                        )
                                    )
                                    viewModel.startCircleLayerRender(
                                        coordinates = listOf(
                                            startLon,
                                            startLat
                                        )
                                    )
                                }
                                Log.d("VALHALLA", "Decoded: ${result}")
                                Log.d("VALHALLA", "Json: ${json}")
                            })

                            viewModel.infoSheet = false
                            viewModel._results.value = emptyList()

                        } else {
                            Toast.makeText(
                                context,
                                "Couldn't get the current location",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onHideClick = {
                        viewModel.isDestClicked = false
                        Log.d("STATEE", "first:" + viewModel.isHideClicked.toString())
                        viewModel.isHideClicked = true
                        Log.d("STATEE", "second:" + viewModel.isHideClicked.toString())
                    },
                    onDestValueUpdate = { dest -> viewModel.updateDestName(dest) },
                    onStartValueUpdate = { start -> viewModel.updateStartName(start) },
                    startState = { state ->
                        viewModel.startIsFocused = state
                    },
                    destState = { state ->
                        viewModel.destIsFocused = state
                    }
                )

                if (viewModel.startIsFocused) {
                    currentLocationChip(
                        name = "Current Location",
                        onPressNavigate = {
                            val currentLat = viewModel.locations.last().latitude
                            val currentLon = viewModel.locations.last().longitude
                            viewModel.currentLocation[0].lat = currentLat
                            viewModel.currentLocation[0].lon = currentLon
                            viewModel.reverseSearch(
                                lat = viewModel.currentLocation[0].lat,
                                lon = viewModel.currentLocation[0].lon
                            )
                        }
                    )
                }
                if (viewModel.destIsFocused) {
                    currentLocationChip(
                        name = "Current Location",
                        onPressNavigate = {
                            val currentLat = viewModel.locations.last().latitude
                            val currentLon = viewModel.locations.last().longitude
                            viewModel.destCord[0].lat = currentLat
                            viewModel.destCord[0].lon = currentLon
                            viewModel.destReverseSearch(
                                lat = currentLat,
                                lon = currentLon
                            )
                        }
                    )
                }

                if (searchList.value.isNotEmpty()) {
                    SearchResultScrollable(
                        currentLocItem = {},
                        list = searchList.value,
                        modifier = Modifier.weight(1f, fill = false),
                        onNavigate = { photonObject ->
                            viewModel.startName = ""
                            viewModel.StartselectedLocation =
                                photonObject.properties.name.toString()
                            viewModel.currentLocation[0].lon = photonObject.geometry.coordinates[0]
                            viewModel.currentLocation[0].lat = photonObject.geometry.coordinates[1]
                            viewModel.searchJob?.cancel()
                            viewModel.searchJob = null
                            viewModel._results.value = emptyList()

                            Log.d("CHANGED", viewModel.currentLocation.toString())

                        }
                    )
                }
                if (destSearchList.value.isNotEmpty()) {
                    SearchResultScrollable(
                        currentLocItem = {},
                        list = destSearchList.value,
                        modifier = Modifier.weight(1f, fill = false),
                        onNavigate = { photonObject ->
                            viewModel.destName = ""
                            viewModel.destSelectedLocation = photonObject.properties.name.toString()
                            viewModel.destCord[0].lon = photonObject.geometry.coordinates[0]
                            viewModel.destCord[0].lat = photonObject.geometry.coordinates[1]
                            viewModel.destSearchJob?.cancel()
                            viewModel.destSearchJob = null
                            viewModel.dest_results.value = emptyList()

                            Log.d("CHANGED", viewModel.currentLocation.toString())

                        }
                    )
                }
            }
            FloatingActionButton(
                containerColor = Color.Black,
                onClick = {
                    viewModel.isDestClicked = false
                    viewModel.isDrawerOpen = false
                    viewModel.isDrawerHidden = false
                    viewModel.lineLayerEraser()
                    viewModel.circleLayerEraser()
                    viewModel.startCircleLayerEraser()
                    viewModel.eraseTimeChip()
                    viewModel.eraseDistChip()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 90.dp, end = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 10.dp,
                            end = 5.dp,
                            bottom = 10.dp
                        ),
                        painter = painterResource(R.drawable.search_24px),
                        contentDescription = "search",
                        tint = Color.White
                    )
                    Text(
                        "Search",
                        Modifier.padding(start = 5.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                        color = Color.White
                    )
                }
            }
        } else if (viewModel.isUserInteracting&&!viewModel.isTripStarted) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .align(Alignment.TopCenter)
            ) {
                MainScreenSearchBar(
                    value = viewModel.searchValue,
                    onValueUpdate = { search -> viewModel.updateSearchValue(search) },
                    padding = 10,
                    onSearchClick = {},
                    onCrossClick = { viewModel.clear() },
                    placeholderValue = "Search"
                )

                if (searchList.value.isNotEmpty()) {
                    SearchResultScrollable(
                        currentLocItem = {},
                        list = searchList.value,
                        modifier = Modifier,
                        onNavigate = { photonObject ->
                            val coordinates = photonObject.geometry.coordinates
                            viewModel.circleLayerRender(coordinates)
                            Log.d("VALHALLA", "photonObject: ${photonObject}")
                            viewModel.destCord.clear()
                            viewModel.destCord.add(
                                ValhallaLocations(
                                    lat = coordinates[1],
                                    lon = coordinates[0]
                                )
                            )
                            viewModel.destSelectedLocation =
                                "${photonObject.properties.name},${photonObject.properties.city},${photonObject.properties.country}"
                            scope.launch {
                                viewModel.pointName(name = photonObject.properties.name.toString())
                                camera.animateTo(
                                    finalPosition = camera.position.copy(
                                        target = Position(
                                            latitude = photonObject.geometry.coordinates[1],
                                            longitude = photonObject.geometry.coordinates[0]
                                        ),
                                        zoom = 16.0
                                    ),
                                    duration = 3.seconds
                                )
                                viewModel.infoSheet = true
                            }
                            viewModel.searchJob?.cancel()
                            viewModel.searchJob = null
                            viewModel._results.value = emptyList()

                        }
                    )
                }
            }
        }
}

    if (viewModel.infoSheet){
        InfoBottomSheet(name = viewModel.pointName
        , onDirectionsClick = {
                if (viewModel.locations!=null){
                    viewModel.isDestClicked = true
                    Log.d("VALHALLA","Current: ${viewModel.locations.last().latitude}, ${viewModel.locations.last().longitude}")
                    Log.d("VALHALLA","Dest: ${viewModel.destCord}")

                    var startLat = viewModel.currentLocation[0].lat
                    var startLon = viewModel.currentLocation[0].lon
                    val currentLat = viewModel.locations.last().latitude
                    val currentLon = viewModel.locations.last().longitude
                    if (startLon == 0.0 && startLat == 0.0) {
                        startLon = currentLon
                        startLat = currentLat
                    }
                    viewModel.reverseSearch(
                            lat =  startLat,
                            lon = startLon
                        )
                        val request = ValhallaTurnByTurnRequest(
                            locations = listOf(
                                ValhallaLocations(
                                    lat = startLat,
                                    lon = startLon
                                ),
                                ValhallaLocations(
                                    lat = viewModel.destCord[0].lat,
                                    lon = viewModel.destCord[0].lon
                                )
                            ),
                            costing = "auto"
                        )
                        viewModel.getTurnByTurnInstructions(request, onSuccess = {response->
                            val shape = response.routes[0].geometry
                            Log.d("VALHALLA","Result: ${response}")
                            val result = viewModel.decodeShape(shape)
                            if(result!=null){
                                val lastStep = response.routes[0].legs[0]
                                val totalDuration = lastStep.duration
                                val Steps = response.routes[0].legs[0].steps
                                val middleStepIndex = Steps.size
                                viewModel.lineLayerRender(result)
                                viewModel.circleLayerRender(listOf(result[result.lastIndex].longitude,result[result.lastIndex].latitude))
                                viewModel.startCircleLayerRender(coordinates = listOf(startLon,startLat))
                                var half = middleStepIndex/2
                                val modulo = half%1
                                if (modulo!=0){
                                    half = half-modulo
                                }
                                val lastStepLocation: List<Double> = lastStep.steps[lastStep.steps.lastIndex-1].maneuver.location
                                val time = viewModel.secToTimeDisplay(totalDuration.toInt())
                                val distance = viewModel.formatDistance(lastStep.distance.toInt())



                                viewModel.updateTimeChip(longitude = lastStepLocation[0] , latitude = lastStepLocation[1], time = time,  dist = lastStep.distance.toString())
                                viewModel.updateDistChip(longitude = lastStep.steps[1].maneuver.location[0] , latitude = lastStep.steps[1].maneuver.location[1], time = time,  dist = distance)

                            }
                        })

                    viewModel.infoSheet = false

                }else{
                    Toast.makeText(context,"Couldn't get the current location", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}