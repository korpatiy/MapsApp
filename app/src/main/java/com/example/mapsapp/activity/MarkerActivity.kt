package com.example.mapsapp.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsapp.R
import com.example.mapsapp.adapter.MarkerViewAdapter
import com.example.mapsapp.model.ImageItem
import com.example.mapsapp.realm.models.ImageRealm
import com.example.mapsapp.realm.models.MarkerRealm
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId

class MarkerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var markerViewAdapter: MarkerViewAdapter
    private lateinit var items: MutableList<ImageItem>
    private var markerRealm: MarkerRealm? = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)
        realm = Realm.getDefaultInstance()

        recyclerView = findViewById(R.id.markerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val markerId = intent.extras?.get("markerId").toString()
        markerRealm =
            realm.where(MarkerRealm::class.java).equalTo(MarkerRealm::id.name, markerId)
                .findFirst()

        if (markerRealm == null) finish()

        items = markerRealm!!.images.map {
            ImageItem(
                imageUri = Uri.fromFile(File(it.uriPath)),
                createDate = LocalDateTime.ofInstant(
                    it.createDate.toInstant(),
                    ZoneId.systemDefault()
                )
            )
        }.toMutableList()

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
                    updateData(fileUri.path!!)
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

    private fun updateData(path: String) {
        val realPath = path.substringAfter('/')
        realm.executeTransaction {
            markerRealm!!.images.add(ImageRealm(uriPath = realPath))
            it.copyToRealmOrUpdate(markerRealm!!)
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
}

