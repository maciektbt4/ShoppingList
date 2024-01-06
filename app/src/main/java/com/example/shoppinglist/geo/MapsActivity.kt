package com.example.shoppinglist.geo

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.example.shoppinglist.R
import com.example.shoppinglist.data.repositories.ShopRepositoryFirebase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.shoppinglist.databinding.ActivityMapsBinding
import com.example.shoppinglist.geo.tracking.GeofenceHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: ShopViewModel
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper


    private val markerList: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)

        val repository = ShopRepositoryFirebase()
        val factory = ShopViewModelFactory(repository)
        viewModel = ViewModelProviders.of(this, factory).get(ShopViewModel::class.java)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.back.setOnClickListener{
            finish()
        }
        binding.shops.setOnClickListener{
            val intent = Intent(this, ShopListActivity::class.java)
            startActivity(intent)
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableUserLocation()


        CoroutineScope(Dispatchers.Main).launch {
            // Clear existing markers
            markerList.forEach { it.remove() }
            markerList.clear()

            val shops = viewModel.getAllShops()
            var id = 0
            shops.forEach{
                Log.d("ShopList",it.name)
                val shop = LatLng(it.latitude, it.longitude)
                val marker = mMap.addMarker(MarkerOptions().position(shop).title(it.name))
                val circleOptions = CircleOptions()
                circleOptions.center(shop)
                circleOptions.radius(it.radius.toDouble())
                circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
                circleOptions.strokeWidth(4.0F)
                circleOptions.fillColor(Color.argb(64, 255, 0, 0))
                mMap.addCircle(circleOptions)
                if(marker != null){
                    markerList.add(marker)
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(shop))
                addGeofence("Geo${id++}", shop, it.radius)
            }
        }

    }

    override fun onRestart() {
        super.onRestart()
        // When the activity is restarted, update the markers again
        if (::mMap.isInitialized) {
            onMapReady(mMap)
        }
    }

    override fun onResume() {
        super.onResume()
        // When the activity is resumed, update the markers again
        if (::mMap.isInitialized) {
            onMapReady(mMap)
        }
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.INTERNET
                ),
                0
            )
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(id:String, latLng: LatLng, radius: Float){
        var geofence = geofenceHelper.getGeofence(id, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        var geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        var pendingIntent = geofenceHelper.getPendingIntent()
        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
            .addOnSuccessListener {
                Log.i("geofences", "OK rejestracja")
            }
            .addOnFailureListener{ exception ->
                if (exception is ApiException) {
                    Log.e("geofences", "Status code: ${exception.statusCode}")
                    if (exception.statusCode == GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE) {
                        Log.e("geofences", "Geofences not available on this device.")
                    } else if (exception.statusCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES) {
                        Log.e("geofences", "Too many geofences.")
                    } else if (exception.statusCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS) {
                        Log.e("geofences", "Too many pending intents.")
                    }
                }
            }
    }
}