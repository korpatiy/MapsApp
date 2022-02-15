package com.example.mapsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsapp.model.ImageItem

class MarkerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)

        recyclerView = findViewById(R.id.markerView)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val itemList = listOf(
            ImageItem(
                imageId = R.drawable.reverbnation
            ),
            ImageItem(
                imageId = R.drawable.video
            )
        )
        val markerViewAdapter = MarkerViewAdapter(itemList)
        recyclerView.adapter = markerViewAdapter
    }
}