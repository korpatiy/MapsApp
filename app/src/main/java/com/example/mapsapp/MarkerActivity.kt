package com.example.mapsapp

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsapp.model.ImageItem
import com.example.mapsapp.model.ItemViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MarkerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var markerViewAdapter: MarkerViewAdapter
    private var items: MutableList<ImageItem> = mutableListOf()
    private var markerId: String = ""

    private val itemsViewModel by lazy { ViewModelProvider(this).get(ItemViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)

        recyclerView = findViewById(R.id.markerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        itemsViewModel.items.observe(this) {
            it?.let {
                markerViewAdapter.notifyItemInserted(items.size)
            }
        }

        val markerId = intent.extras?.get("markerId").toString()
        itemsViewModel.items.value?.let {
            val markerItems = it[markerId]
            if (markerItems == null) {
                it[markerId] = mutableListOf()
            }
            items = it[markerId]!!
        }
        markerViewAdapter = MarkerViewAdapter(items)
        recyclerView.adapter = markerViewAdapter
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener(this)
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    items.add(ImageItem(fileUri))
                    markerViewAdapter.notifyItemInserted(items.size)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onClick(v: View?) {
        ImagePicker.with(this)
            .cameraOnly()
            .saveDir(File(filesDir, "ImagePicker"))
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { cameraLauncher.launch(it) }
    }

    override fun onBackPressed() {
        itemsViewModel.updateData(markerId, items)
        finish()
    }
}

