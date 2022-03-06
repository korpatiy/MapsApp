package com.example.mapsapp.model

import android.net.Uri
import java.time.LocalDateTime

class ImageItem(
    val imageUri: Uri,
    val createDate: LocalDateTime = LocalDateTime.now()
)