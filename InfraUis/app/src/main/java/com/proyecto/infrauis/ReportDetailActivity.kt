package com.proyecto.infrauis

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class ReportDetailActivity : ComponentActivity() {

    private lateinit var reportName: String
    private lateinit var reportDescription: String
    private lateinit var reportLocation: String
    private lateinit var reportImageUrl: String
    private lateinit var reportStatus: String // Added status field
    private lateinit var database: DatabaseReference
    private var configPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        val reportImageView: ImageView = findViewById(R.id.reportDetailImageView)
        val reportNameTextView: TextView = findViewById(R.id.reportDetailName)
        val reportDescriptionTextView: TextView = findViewById(R.id.reportDetailDescription)
        val reportLocationTextView: TextView = findViewById(R.id.reportDetailLocation)
        val reportStatusTextView: TextView = findViewById(R.id.reportDetailStatus) // Status TextView
        val modifyReportButton: Button = findViewById(R.id.modifyReportButton) // Renamed and adjusted

        reportName = intent.getStringExtra("REPORT_NAME").toString()
        reportDescription = intent.getStringExtra("REPORT_DESCRIPTION").toString()
        reportLocation = intent.getStringExtra("REPORT_LOCATION").toString()
        reportImageUrl = intent.getStringExtra("REPORT_IMAGE_URL").toString()
        reportStatus = intent.getStringExtra("REPORT_STATUS").toString() // Retrieve status

        reportNameTextView.text = reportName
        reportDescriptionTextView.text = reportDescription
        reportLocationTextView.text = reportLocation
        reportStatusTextView.text = "Estado: $reportStatus" // Set status text

        if (reportImageUrl.isNotEmpty()) {
            Glide.with(this).load(reportImageUrl).into(reportImageView)
        } else {
            reportImageView.setImageResource(R.drawable.logo) // Placeholder if no image URL
        }

        // Obtain password from database
        database = FirebaseDatabase.getInstance().reference.child("Config")
        database.child("password").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                configPassword = snapshot.getValue(String::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReportDetailActivity, "Error fetching configuration", Toast.LENGTH_SHORT).show()
            }
        })

        modifyReportButton.setOnClickListener {
            showPasswordDialog()
        }

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val reportButton = findViewById<ImageButton>(R.id.reportButton)
        val viewReportButton = findViewById<ImageButton>(R.id.viewReportButton)
        val profileButton = findViewById<ImageButton>(R.id.profileButton)

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        reportButton.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
        viewReportButton.setOnClickListener {
            startActivity(Intent(this, ViewReportActivity::class.java))
        }
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun showPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter Password")
            .setPositiveButton("Submit") { _, _ ->
                val enteredPassword = passwordEditText.text.toString()
                if (enteredPassword == configPassword) {
                    val intent = Intent(this, ModifyReportActivity::class.java).apply {
                        putExtra("REPORT_NAME", reportName)
                        putExtra("REPORT_DESCRIPTION", reportDescription)
                        putExtra("REPORT_LOCATION", reportLocation)
                        putExtra("REPORT_STATUS", reportStatus)
                        putExtra("REPORT_IMAGE_URL", reportImageUrl) // Pass the image URL
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

}
