package com.example.mapsapp.realm.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class MarkerRealm(
    @PrimaryKey
    var id: String = ObjectId().toHexString(),

    var latitude: Double = 0.0,

    var longitude: Double = 0.0,

    var images: RealmList<ImageRealm> = RealmList()
) : RealmObject()