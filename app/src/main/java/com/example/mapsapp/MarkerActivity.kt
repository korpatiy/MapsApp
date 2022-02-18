package com.example.mapsapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsapp.model.ImageItem
import com.example.mapsapp.model.ItemViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it?.let {
                if (it.resultCode == Activity.RESULT_OK) {
                    val bitmap = it.data?.extras?.get("data") as Bitmap
                    items.add(ImageItem(bitmap))
                    markerViewAdapter.notifyItemInserted(items.size)
                } else {
                    Toast.makeText(applicationContext, "Image not clicked", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    override fun onClick(v: View?) {
        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        //itemList.add(ImageItem(imageId = com.google.android.gms.base.R.drawable.googleg_disabled_color_18))
        //markerViewAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        itemsViewModel.updateData(markerId, items)
        finish()
    }
}

