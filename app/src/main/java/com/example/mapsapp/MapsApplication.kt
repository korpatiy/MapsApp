package com.example.mapsapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import io.realm.Realm
import io.realm.RealmConfiguration

class MapsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                //.deleteRealmIfMigrationNeeded()
                .build()
        )

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "LocationServiceChannel"
        val descriptionText = "Channel for location service"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "LOCATION_SERVICE"
    }
}