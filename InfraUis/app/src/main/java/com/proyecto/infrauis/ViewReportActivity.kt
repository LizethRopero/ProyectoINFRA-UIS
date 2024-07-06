package com.proyecto.infrauis

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.ComponentActivity
import com.google.firebase.database.*

class ViewReportActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var reportsListView: ListView
    private lateinit var reports: MutableList<Report>
    private lateinit var adapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewreport)

        database = FirebaseDatabase.getInstance().reference.child("reports")
        reportsListView = findViewById(R.id.reportsListView)
        reports = mutableListOf()
        adapter = ReportAdapter(this, reports)
        reportsListView.adapter = adapter

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reports.clear()
                for (reportSnapshot in snapshot.children) {
                    val report = reportSnapshot.getValue(Report::class.java)
                    if (report != null) {
                        reports.add(report)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })

        reportsListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedReport = reports[position]
            val intent = Intent(this, ReportDetailActivity::class.java).apply {
                putExtra("REPORT_NAME", selectedReport.name)
                putExtra("REPORT_DESCRIPTION", selectedReport.description)
                putExtra("REPORT_LOCATION", selectedReport.location)
                putExtra("REPORT_IMAGE_URL", selectedReport.imageUrl)
                putExtra("REPORT_STATUS", selectedReport.status)
            }
            startActivity(intent)
        }


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
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
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
