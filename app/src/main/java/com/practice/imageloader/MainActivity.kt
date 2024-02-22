package com.practice.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.practice.imageloader.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val imageUrl =
        "https://img.freepik.com/free-photo/fresh-yellow-daisy-single-flower-close-up-beauty-generated-by-ai_188544-15543.jpg"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        coroutineScope.launch {
            val originalDeferred = coroutineScope.async(Dispatchers.IO) {
                getOriginalBitmap()
            }
            val originalBitmap = originalDeferred.await()
            val filterDeferred =
                coroutineScope.async(Dispatchers.Default) { applyFilter(originalBitmap) }
            val filteredBmp = filterDeferred.await()
            loadImage(filteredBmp)
        }
    }

    private fun getOriginalBitmap(): Bitmap {
        return URL(imageUrl).openStream().use {
            BitmapFactory.decodeStream(it)
        }
    }

    private fun loadImage(bmp: Bitmap) {
        binding.progressBar.visibility = View.GONE
        binding.imageView.setImageBitmap(bmp)
        binding.imageView.visibility = View.VISIBLE
    }

    private fun applyFilter(originalBmp: Bitmap) = Filter.apply(originalBmp)
}