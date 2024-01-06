package com.example.shoppinglist.geo.tracking

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

class GeofenceHelper(base: Context?) : ContextWrapper(base) {

    private val TAG: String = "GeofenceHelper"
    private lateinit var pendingIntent: PendingIntent

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest{
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeofence(ID: String, latLang: LatLng, radius: Float, transitionTypes: Int): Geofence{
        return Geofence.Builder()
            .setCircularRegion(latLang.latitude,latLang.longitude,radius)
            .setRequestId(ID)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getPendingIntent(): PendingIntent{

        try{
            val intent = Intent(this, GeofenceReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(
                this, 2607, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            return pendingIntent
        }catch(e: Exception) {
            Log.e("Geofence Alert", "Error: ${e.message}")
        }
        return pendingIntent
    }
}