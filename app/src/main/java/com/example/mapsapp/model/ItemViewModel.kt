package com.example.mapsapp.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {

    val items: MutableLiveData<MutableMap<String, MutableList<ImageItem>>> = MutableLiveData()

    init {
        items.value = Data.items
    }

    fun updateData(markerId: String, items: MutableList<ImageItem>) {
        this.items.value?.put(markerId, items)
        Data.items[markerId] = items
    }
}

object Data {
    val items: MutableMap<String, MutableList<ImageItem>> = mutableMapOf()
}