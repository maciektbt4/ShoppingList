package com.example.shoppinglist.geo.tracking

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProviders
import com.example.shoppinglist.R
import com.example.shoppinglist.data.repositories.ShopRepositoryFirebase
import com.example.shoppinglist.geo.ShopViewModel
import com.example.shoppinglist.geo.ShopViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest

class LocationService: Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private var notificationManager: NotificationManager? = null
    private var updateNotification: NotificationCompat.Builder? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
            )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
            ACTION_ENTER_NOTIFICATION -> enterLocation()
            ACTION_EXIT_NOTIFICATION -> exitLocation()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )


        val notification = NotificationCompat.Builder(this, "location")
            .setContentText("Tracking location...")
            .setContentText("Location: null")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager


//        locationClient.getLocationUpdates(1000L)
//            .catch { e -> e.printStackTrace()}
//            .onEach { location ->
//                val lat = location.latitude.toString()
//                val long = location.longitude.toString()
//                val updateNotification = notification.setContentText(
//                    "Location: ($lat, $long)"
//                )
//                Log.i(
//                    "location-fused",
//                    "Nowe położenie: ${lat}, ${long}"
//                )
//
//                notificationManager.notify(1, updateNotification.build())
//            }
//            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun enterLocation() {
        // Update the notification content
        updateNotification?.setContentText("You are entering shop location")

        // Notify the NotificationManager to update the notification
        notificationManager?.notify(1, updateNotification?.build())
    }

    private fun exitLocation() {
        // Update the notification content
        updateNotification?.setContentText("You are exiting shop location")

        // Notify the NotificationManager to update the notification
        notificationManager?.notify(1, updateNotification?.build())
    }

    private fun stop(){
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_ENTER_NOTIFICATION = "ACTION_ENTER_NOTIFICATION"
        const val ACTION_EXIT_NOTIFICATION = "ACTION_ENTER_NOTIFICATION"

    }


}