package com.szkarad.szkaradapp.map

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.szkarad.szkaradapp.common.CommonComposables
import com.szkarad.szkaradapp.favouriteshops.ShopDialog
import com.szkarad.szkaradapp.firebasedb.shopdb.Shop
import com.szkarad.szkaradapp.firebasedb.shopdb.ShopViewModel
import com.szkarad.szkaradapp.ui.theme.SzkaradAppTheme

class ShopsMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SzkaradAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user!!.uid
                    val svm = ShopViewModel(application, uid)
                    var hasLocationPermission by remember {
                        mutableStateOf(checkForPermission(this))
                    }
                    val context = this

                    Column {
                        CommonComposables.CommonTopBar()
                        if (hasLocationPermission) {
                            MapScreen(context, svm)
                        } else {
                            LocationPermissionScreen {
                                hasLocationPermission = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapScreen(context: Context, svm: ShopViewModel) {
    var showMap by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var mapProperties by remember { mutableStateOf(MapProperties()) }

    getCurrentLocation(context) {
        location = it
        showMap = true
    }

    if (showMap) {
        MyMap(
            latLng = location,
            mapProperties = mapProperties,
            onChangeMapType = {
                mapProperties = mapProperties.copy(mapType = it)
            },
            svm)
    } else {
        Text(text = "Loading Map...")
    }
}

@Composable
fun MyMap(
    latLng: LatLng,
    mapProperties: MapProperties = MapProperties(),
    onChangeMapType: (mapType: MapType) -> Unit,
    svm: ShopViewModel
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }
    val shops by svm.shops.collectAsState(emptyList())
    var showAddItemDialog by remember { mutableStateOf(false) }
    var clickedLocation by remember {
        mutableStateOf(LatLng(0.0,0.0))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapClick = {
                clickedLocation = it
                showAddItemDialog = true
            }
        ) {
            shops.toList().forEach {
                val latLngShop = LatLng(it.latitude, it.longitude)
                Marker(
                    state = MarkerState(position = latLngShop),
                    title = it.name,
                    snippet = it.description,
                )
            }
        }
        ChangeMapDropdown(onChangeMapType)
    }

    if (showAddItemDialog) {
        ShopDialog(
            title = "Add Shop",
            initialName = "",
            initialDesc = "",
            onConfirm = { name, description ->
                    val shop = Shop(
                        id = "",
                        name = name,
                        description = description,
                        radius = 100.0,
                        latitude = clickedLocation.latitude,
                        longitude = clickedLocation.longitude
                    )
                    svm.insertShop(shop) { id ->
                        shop.id = id
                        svm.updateShop(shop)
                    }
                    showAddItemDialog = false
            },
            onDismiss = { showAddItemDialog = false }
        )
    }
}

@Composable
fun ChangeMapDropdown(onChangeMapType: (mapType: MapType) -> Unit) {
    var mapTypeMenuExpanded by remember { mutableStateOf(false) }
    var mapTypeMenuSelectedText by remember {
        mutableStateOf(
            MapType.NORMAL.name.capitaliseIt()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Row {
            Button(onClick = { mapTypeMenuExpanded = true }) {
                Text(text = mapTypeMenuSelectedText)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown arrow",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
            DropdownMenu(expanded = mapTypeMenuExpanded,
                onDismissRequest = { mapTypeMenuExpanded = false }) {
                MapType.values().forEach {
                    val mapType = it.name.capitaliseIt()
                    DropdownMenuItem(text = {
                        Text(text = mapType)
                    }, onClick = {
                        onChangeMapType(it)
                        mapTypeMenuSelectedText = mapType
                        mapTypeMenuExpanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun LocationPermissionScreen(
    onPermissionGranted: () -> Unit
) {
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var isGranted = true
        permissions.entries.forEach {
            if (!it.value) {
                isGranted = false
                return@forEach
            }
        }

        if (isGranted) {
            onPermissionGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Location Permission Required",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
        ) {
            Text(text = "Grant Location Permission")
        }
    }
}
