package com.proyecto.infrauis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper

class HomeActivity : ComponentActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val newsWebView: WebView = findViewById(R.id.newsWebView)
        newsWebView.webViewClient = WebViewClient()
        newsWebView.loadUrl("https://comunicaciones.uis.edu.co/")

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val reportButton = findViewById<ImageButton>(R.id.reportButton)
        val viewReportButton = findViewById<ImageButton>(R.id.viewReportButton)
        val profileButton = findViewById<ImageButton>(R.id.profileButton)
        val blueFilter = android.graphics.PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP)
        val handler = Handler(Looper.getMainLooper())

        homeButton.setOnClickListener {
            // Apply the blue color filter
            homeButton.colorFilter = blueFilter
            // Schedule to remove the blue color filter after 0.2 seconds
            handler.postDelayed({
                homeButton.clearColorFilter()
            }, 100)
        }
        reportButton.setOnClickListener {
            // Apply the blue color filter
            reportButton.colorFilter = blueFilter
            // Schedule to remove the blue color filter after 0.2 seconds
            handler.postDelayed({
                reportButton.clearColorFilter()
            }, 100)
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
        viewReportButton.setOnClickListener {
            // Apply the blue color filter
            viewReportButton.colorFilter = blueFilter
            // Schedule to remove the blue color filter after 0.2 seconds
            handler.postDelayed({
                viewReportButton.clearColorFilter()
            }, 100)
            val intent = Intent(this, ViewReportActivity::class.java)
            startActivity(intent)
        }
        profileButton.setOnClickListener {
            // Apply the blue color filter
            profileButton.colorFilter = blueFilter
            // Schedule to remove the blue color filter after 0.2 seconds
            handler.postDelayed({
                profileButton.clearColorFilter()
            }, 100)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
