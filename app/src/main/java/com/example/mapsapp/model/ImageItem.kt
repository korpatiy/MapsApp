package com.example.mapsapp.model

import android.net.Uri
import java.time.LocalDateTime

class ImageItem(
    val image: Uri,
    val createDate: LocalDateTime = LocalDateTime.now()
)