package com.szkarad.szkaradapp.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geoEvent = GeofencingEvent.fromIntent(intent)
        if (geoEvent != null) {
            for (geo in geoEvent.triggeringGeofences!!) {
                when (geoEvent.geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Toast.makeText(context, "Wkroczyłeś w obszar: ${geo.requestId}", Toast.LENGTH_LONG).show()
                    }
                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Toast.makeText(context, "Wyszedłeś z obszaru: ${geo.requestId}", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }

    }
}