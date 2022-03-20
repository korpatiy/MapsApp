package com.example.mapsapp.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mapsapp.MapsApplication.Companion.CHANNEL_ID
import com.example.mapsapp.R
import com.example.mapsapp.activity.MapsActivity
import com.example.mapsapp.realm.models.MarkerRealm
import io.realm.Realm


class LocationService : Service() {

    private lateinit var markers: List<MarkerRealm>
    private lateinit var realm: Realm

    companion object {
        private const val NOTIFICATION_ID = 101
        private const val RADIUS_DETECT_DISTANCE = 50F
    }

    private inner class MyLocationListener : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            val marker = markers.firstOrNull {
                val markerLocation = Location("").apply {
                    latitude = it.latitude
                    longitude = it.longitude
                }
                location.distanceTo(markerLocation) <= RADIUS_DETECT_DISTANCE
            }
            marker?.let {
                showNotification("${marker.latitude} - ${marker.longitude}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND).apply {
            start()

            val myLocationListener = MyLocationListener()
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, RADIUS_DETECT_DISTANCE, myLocationListener
            )
            realm = Realm.getDefaultInstance()
        }
        Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        markers = realm.where(MarkerRealm::class.java).findAll().toList()
        return START_STICKY
    }

    /*private fun someTask() {
         val markers = realm.where(MarkerRealm::class.java).findAll()

         Thread {
             for (i in 1..5) {
                 Log.d(ContentValues.TAG, "i = $i")
                 showNotification()
                 try {
                     TimeUnit.SECONDS.sleep(5)
                 } catch (e: InterruptedException) {
                     e.printStackTrace()
                 }
             }
             stopSelf()
         }.start()
     }*/

    private fun showNotification(markerName: String) {
        val pendingIntent: PendingIntent =
            Intent(this, MapsActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.location_marker)
            .setContentTitle("Рядом точка!")
            .setContentText(markerName)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        //startForeground(NOTIFICATION_ID, builder.build())

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }
}