package com.example.mapsapp.model

import android.graphics.Bitmap
import java.time.LocalDateTime

class ImageItem(
    val image: Bitmap,
    val createDate: LocalDateTime = LocalDateTime.now()
)