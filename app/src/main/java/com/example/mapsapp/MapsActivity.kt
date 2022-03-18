package com.example.mapsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapsapp.databinding.ActivityMapsBinding
import com.example.mapsapp.realm.models.MarkerRealm
import com.github.dhaval2404.imagepicker.util.PermissionUtil
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
    private lateinit var currentLocation: Location

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true
        setUpMap()

        val baseMarker = LatLng(58.01041829322895, 56.22591963195325)
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(baseMarker, 15F))
        drawMarkers()
        gMap.setOnMapClickListener(this)
        gMap.setOnMarkerClickListener(this)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun setUpMap() {
        checkPermissions()
    /*    gMap.isMyLocationEnabled = true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            gMap.addMarker(
                MarkerOptions().position(LatLng(it.latitude, it.longitude))
                    .title("${it.latitude} - ${it.longitude}")

            )
        }*/

    }

    private fun checkPermissions() {

     /*   if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true);
        }*/

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
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