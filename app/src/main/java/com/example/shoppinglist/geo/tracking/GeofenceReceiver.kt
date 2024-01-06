package com.example.shoppinglist.geo.tracking

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.shoppinglist.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geoEvent = GeofencingEvent.fromIntent(intent)
        val triggering = geoEvent?.triggeringGeofences
        if (triggering != null) {
            for (geo in triggering) {
                Log.i("geofence", "Geofence with id: ${geo.requestId} is active.")
            }

            if (geoEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                // Update notification when geofence transition is triggered
                context.startService(Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_ENTER_NOTIFICATION
                })
                Log.i("geofences", "Entered geofence: ${geoEvent.triggeringLocation.toString()}")
            } else if (geoEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                context.startService(Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_EXIT_NOTIFICATION
                })
                Log.i("geofences", "Exited geofence: ${geoEvent.triggeringLocation.toString()}")
            } else {
                Log.e("geofences", "Error.")
            }
        }
    }
}
