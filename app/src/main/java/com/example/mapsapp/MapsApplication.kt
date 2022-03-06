package com.example.mapsapp

import android.app.Application
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
    }
}