package com.example.saifb

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewTopDesigns: RecyclerView
    private lateinit var recyclerViewNewDesigns: RecyclerView
    private lateinit var viewPager: ViewPager

    // Separate ProgressBars for each component
    private lateinit var progressBarTopDesigns: ProgressBar
    private lateinit var progressBarNewDesigns: ProgressBar
    private lateinit var progressBarViewPager: ProgressBar

    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        recyclerViewTopDesigns = findViewById(R.id.recyclerViewTopDesigns)
        recyclerViewNewDesigns = findViewById(R.id.recyclerViewNewDesigns)
        viewPager = findViewById(R.id.viewPager)

        // Initialize ProgressBars
        progressBarTopDesigns = findViewById(R.id.progressBarTopDesigns)
        progressBarNewDesigns = findViewById(R.id.progressBarNewDesigns)
        progressBarViewPager = findViewById(R.id.progressBarViewPager)

        // Show each ProgressBar initially for 3 seconds
        showProgressBar(progressBarTopDesigns)
        showProgressBar(progressBarNewDesigns)
        showProgressBar(progressBarViewPager)

        // Set up image lists for RecyclerViews
        val topDesignsImages = listOf(
            SliderImage(R.drawable.banner2),
            SliderImage(R.drawable.design4),
            SliderImage(R.drawable.banner1)
        )

        val newDesignsImages = listOf(
            SliderImage(R.drawable.design1),
            SliderImage(R.drawable.design2),
            SliderImage(R.drawable.design3)
        )

        val viewPagerImages = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

        // Set up adapters for RecyclerViews
        setUpRecyclerView(recyclerViewTopDesigns, topDesignsImages)
        setUpRecyclerView(recyclerViewNewDesigns, newDesignsImages)

        // Set up ViewPager with ViewPagerAdapter
        val viewPagerAdapter = ViewPagerAdapter(this, viewPagerImages)
        viewPager.adapter = viewPagerAdapter

        // Start auto-slide for ViewPager
        startAutoSlide(viewPagerImages.size)
    }

    // Function to set up RecyclerView
    private fun setUpRecyclerView(recyclerView: RecyclerView, images: List<SliderImage>) {
        val adapter = SliderAdapter(this, images)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    // Function to start auto-slide for ViewPager
    private fun startAutoSlide(imageCount: Int) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage == imageCount) {
                    currentPage = 0
                }
                viewPager.currentItem = currentPage++
                handler.postDelayed(this, 2000) // Change image every 2 seconds
            }
        }, 2000)
    }

    // Function to show ProgressBar and hide it after 3 seconds
    private fun showProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        handler.postDelayed({
            progressBar.visibility = View.GONE
        }, 3000) // Hide progress bar after 3 seconds
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null) // Stop auto-slide on activity destroy
        super.onDestroy()
    }
}
