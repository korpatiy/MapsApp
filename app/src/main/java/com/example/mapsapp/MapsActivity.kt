package com.example.mapsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mapsapp.databinding.ActivityMapsBinding
import com.example.mapsapp.realm.models.MarkerRealm
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {

    private lateinit var gMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()

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
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(baseMarker, 15F))

        drawMarkers()
        gMap.setOnMapClickListener(this)
        gMap.setOnMarkerClickListener(this)
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