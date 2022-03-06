package com.example.mapsapp.realm.models

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId
import java.util.*

open class ImageRealm(
    @PrimaryKey
    var id: String = ObjectId().toHexString(),

    @Required
    var uriPath: String = "",

    @Required
    var createDate: Date = Date(),

    /*@LinkingObjects("images")
    val owner: RealmResults<MarkerRealm>? = null*/
) : RealmObject()