package com.example.mapsapp.model

import java.time.LocalDate

class ImageItem(
    val imageId: Int,
    val createDate: LocalDate = LocalDate.now()
)