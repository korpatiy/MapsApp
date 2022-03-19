package com.example.mapsapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapsapp.R
import com.example.mapsapp.databinding.ActivityMapsBinding
import com.example.mapsapp.realm.models.MarkerRealm
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {

    private lateinit var gMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var realm: Realm

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var lastKnownLocation: Location? = null
    private val baseLocation = LatLng(58.01041829322895, 56.22591963195325)
    private var locationPermissionGranted = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_ZOOM = 15F
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
/*

        Intent(this, HelloService::class.java).also { intent ->
            startService(intent)
        }
*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private class MyLocationListener : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            //location.distanceTo()
            //println("${location.latitude} - ${location.longitude}")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true

        drawMarkers()

        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()

        val myLocationListener = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000, 10F, myLocationListener
        )
        gMap.setOnMapClickListener(this)
        gMap.setOnMarkerClickListener(this)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                gMap.isMyLocationEnabled = true
                gMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                gMap.isMyLocationEnabled = false
                gMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                //getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            gMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        gMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(baseLocation, DEFAULT_ZOOM)
                        )
                        gMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            locationPermissionGranted = true
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
        updateLocationUI()
    }

    private fun drawMarkers() {
        val markers = realm.where(MarkerRealm::class.java).findAll()
        markers.forEach {
            gMap.addMarker(
                MarkerOptions().position(LatLng(it.latitude, it.longitude))
                    .title("${it.latitude} - ${it.longitude}")
            )
        }

        if (markers.isNotEmpty()) {
            val baseMarker = markers.last()!!
            gMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        baseMarker.latitude,
                        baseMarker.longitude
                    ), 15F
                )
            )
        }
    }

    override fun onMapClick(latLng: LatLng) {
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
        gMap.addMarker(
            MarkerOptions().position(latLng).title("${latLng.latitude} - ${latLng.longitude}")
        )
        realm.executeTransactionAsync {
            it.insert(MarkerRealm(latitude = latLng.latitude, longitude = latLng.longitude))
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val position = marker.position
        val markerDB = realm.where(MarkerRealm::class.java)
            .equalTo(MarkerRealm::latitude.name, position.latitude)
            .equalTo(MarkerRealm::longitude.name, position.longitude)
            .findFirst()

        markerDB?.let {
            val intent = Intent(this, MarkerActivity::class.java)
            intent.putExtra("markerId", it.id)
            startActivity(intent)
        }
        return false
    }
}