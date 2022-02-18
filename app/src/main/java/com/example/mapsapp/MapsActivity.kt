package com.example.mapsapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.mapsapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {

    private lateinit var gMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var markerList: ArrayList<LatLng> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val baseMarker = LatLng(58.01041829322895, 56.22591963195325)
        gMap.addMarker(MarkerOptions().position(baseMarker).title("Perm city"))
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(baseMarker, 15F))

        drawMarkers()
        gMap.setOnMapClickListener(this)
        gMap.setOnMarkerClickListener(this)
    }

    private fun drawMarkers() {
        markerList.forEach {
            gMap.addMarker(
                MarkerOptions().position(it).title("${it.latitude} - ${it.longitude}")
            )
        }
    }

    override fun onMapClick(latLng: LatLng) {
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
        gMap.addMarker(
            MarkerOptions().position(latLng).title("${latLng.latitude} - ${latLng.longitude}")
        )
        markerList.add(latLng)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(this, MarkerActivity::class.java)
        intent.putExtra("markerId", marker.id)
        startActivity(intent)
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("markers", markerList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        markerList = savedInstanceState.getParcelableArrayList("markers") ?: arrayListOf()
    }
}