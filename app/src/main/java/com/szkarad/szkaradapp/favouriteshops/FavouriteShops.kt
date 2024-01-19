package com.szkarad.szkaradapp.favouriteshops

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.szkarad.szkaradapp.common.CommonComposables
import com.szkarad.szkaradapp.firebasedb.shopdb.Shop
import com.szkarad.szkaradapp.firebasedb.shopdb.ShopViewModel
import com.szkarad.szkaradapp.map.GeoReceiver
import com.szkarad.szkaradapp.map.getCurrentLocation
import com.szkarad.szkaradapp.shoppinglist.ActionButton
import com.szkarad.szkaradapp.ui.theme.SzkaradAppTheme

class FavouriteShops : ComponentActivity() {
    private var selectedShopId by mutableStateOf<String?>(null)
    private lateinit var geoReceiver: GeoReceiver

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SzkaradAppTheme {
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user!!.uid
                val svm = ShopViewModel(application, uid)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    ShopsListScreen(svm, selectedShopId)
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction("com.szkarad.szkaradapp.ACTION_GEOFENCE_TRANSITION")
        }
        geoReceiver = GeoReceiver()
        registerReceiver(geoReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(geoReceiver)
    }
}

@Composable
fun ShopsListScreen(svm: ShopViewModel, selectedShopId: String?) {
    Column {
        CommonComposables.CommonTopBar()
        ShopsListColumn(svm, selectedShopId)
    }
}

@Composable
fun ShopsListColumn(svm: ShopViewModel, selectedShopId: String?) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        bottomBar = { ListManagementRow(svm) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            CommonComposables.WelcomeText(
                text = "Here are your favourite shops!",
                MaterialTheme.colorScheme.onSecondary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ShopsList(svm, selectedShopId)
        }
    }
}

@Composable
fun ListManagementRow(svm: ShopViewModel) {
    val context = LocalContext.current
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    // GEO FENCE STUFF:
    var geoID by remember { mutableIntStateOf(0) }
    val geoClient = LocationServices.getGeofencingClient(context)

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Confirm Clear") },
            text = { Text("Are you sure that you want to clear the list?") },
            confirmButton = {
                Button(onClick = {
                    svm.deleteAllShops()
                    showClearConfirmDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showClearConfirmDialog = false }) {
                    Text("No")
                }
            },
        )
    }

    if (showAddItemDialog) {
        ShopDialog(
            title = "Add Shop",
            initialName = "",
            initialDesc = "",
            onConfirm = { name, description ->
                getCurrentLocation(context) {
                    val shop = Shop(
                        id = "",
                        name = name,
                        description = description,
                        radius = 100.0,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                    svm.insertShop(shop) { id ->
                        shop.id = id
                        svm.updateShop(shop)
                    }

                    geoID = handleGeoFence(context, it, geoID, geoClient)

                    showAddItemDialog = false
                }
            },
            onDismiss = { showAddItemDialog = false }
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionButton(text = "Clear List", onClick = { showClearConfirmDialog = true })
        ActionButton(text = "Add Item", onClick = { showAddItemDialog = true })
    }
}

@SuppressLint("MissingPermission")
fun handleGeoFence(
    context: Context,
    location: LatLng,
    geoID: Int,
    geoClient: GeofencingClient
): Int {
    val geo = Geofence.Builder()
        .setCircularRegion(
            location.latitude,
            location.longitude,
            100F
        )
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setRequestId("GeoId: $geoID")
        .setTransitionTypes(
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        .build()
    val request = GeofencingRequest.Builder()
        .addGeofence(geo)
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        .build()
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, GeoReceiver::class.java).apply {
            action = "com.szkarad.szkaradapp.ACTION_GEOFENCE_TRANSITION"
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    geoClient.addGeofences(request, pendingIntent)
        .addOnSuccessListener {
            Log.i("Geofence", "Dodano geofence z id: ${geo.requestId}")
        }
        .addOnFailureListener { exc ->
            Log.e("LocationError", exc.message.toString())
        }
    return geoID + 1
}

@Composable
fun ShopsList(svm: ShopViewModel, selectedShopId: String?) {
    val shops by svm.shops.collectAsState(emptyList())
    var selectedShop by remember { mutableStateOf<Shop?>(null) }

    LaunchedEffect(selectedShopId) {
        if (selectedShopId != null && selectedShopId != "") {
            svm.getShopById(selectedShopId) { shop ->
                selectedShop = shop
            }
        }
    }

    if (selectedShop != null) {
        EditShopDialog(
            shop = selectedShop!!,
            onDismiss = { selectedShop = null },
            svm = svm
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        items(shops) { shop ->
            ShopRow(shop, svm, onEditClick = { selectedShop = shop })
        }
    }
}

@Composable
fun ShopRow(shop: Shop, svm: ShopViewModel, onEditClick: () -> Unit) {

    Surface(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShopDescription(shop, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            ShopSettings(shop, svm, onEditClick)
        }
    }
}

@Composable
fun ShopSettings(shop: Shop, svm: ShopViewModel, onEditClick: () -> Unit) {
    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Outlined.Edit, "Edit", tint = MaterialTheme.colorScheme.onPrimary)
    }
    IconButton(onClick = { svm.deleteShop(shop) }, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun ShopDescription(shop: Shop, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "${shop.name}: ${shop.description}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        // TODO: Consider adding some more information
        /*Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "position: ${shop.geoPositionX}, ${shop.geoPositionY}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )*/
    }
}

@Composable
fun ShopDialog(
    title: String,
    initialName: String,
    initialDesc: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    isError = name.isBlank() || name.length > 50,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                errorMessage?.let { Text(it, color = Color.Red) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || name.length > 50) {
                        errorMessage = "Invalid name"
                    } else {
                        onConfirm(name, desc)
                    }
                }
            ) { Text("Confirm") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun EditShopDialog(shop: Shop, onDismiss: () -> Unit, svm: ShopViewModel) {
    ShopDialog(
        title = "Edit Shop",
        initialName = shop.name,
        initialDesc = shop.description,
        // TODO: Add changing location
        onConfirm = { newName, newDesc ->
            svm.updateShop(
                shop.copy(
                    name = newName,
                    description = newDesc
                )
            )
            onDismiss()
        },
        onDismiss = onDismiss
    )
}