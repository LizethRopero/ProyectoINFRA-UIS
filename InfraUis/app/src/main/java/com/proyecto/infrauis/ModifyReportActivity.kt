package com.proyecto.infrauis

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ModifyReportActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null
    private lateinit var selectedImageNameTextView: TextView
    private lateinit var locationSpinner: Spinner
    private lateinit var statusSpinner: Spinner
    private lateinit var reportNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var reportId: String
    private lateinit var reportName: String
    private lateinit var reportDescription: String
    private lateinit var reportLocation: String
    private lateinit var reportStatus: String
    private lateinit var reportImageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_report)

        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        val reportImageView = findViewById<ImageView>(R.id.reportModifyImageView)
        reportNameEditText = findViewById(R.id.reportModifyNameEditText)
        descriptionEditText = findViewById(R.id.reportModifyDescriptionEditText)
        selectedImageNameTextView = findViewById(R.id.selectedModifyImageNameTextView)
        locationSpinner = findViewById(R.id.modifyLocationSpinner)
        statusSpinner = findViewById(R.id.statusSpinner)

        // Receive data from intent
        val intent = intent
        reportName = intent.getStringExtra("REPORT_NAME").toString()
        reportDescription = intent.getStringExtra("REPORT_DESCRIPTION").toString()
        reportLocation = intent.getStringExtra("REPORT_LOCATION").toString()
        reportStatus = intent.getStringExtra("REPORT_STATUS").toString()
        reportImageUrl = intent.getStringExtra("REPORT_IMAGE_URL").toString()

        // Load data into UI elements
        reportNameEditText.setText(reportName)
        descriptionEditText.setText(reportDescription)

        if (reportImageUrl.isNotEmpty()) {
            Glide.with(this).load(reportImageUrl).into(reportImageView)
        } else {
            reportImageView.setImageResource(R.drawable.logo) // Placeholder if no image URL
        }

        // Setup location spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.ubicaciones_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        }
        locationSpinner.setSelection(getIndex(locationSpinner, reportLocation))

        // Setup status spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.estados_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            statusSpinner.adapter = adapter
        }
        statusSpinner.setSelection(getIndex(statusSpinner, reportStatus))

        // Setup button click listeners
        val attachModifyPhotoButton = findViewById<Button>(R.id.attachModifyPhotoButton)
        attachModifyPhotoButton.setOnClickListener {
            selectImage()
        }

        val saveModifyButton = findViewById<Button>(R.id.saveChangesButton)
        saveModifyButton.setOnClickListener {
            updateReport()
        }

        val cancelModifyButton = findViewById<Button>(R.id.CancelReportButton)
        cancelModifyButton.setOnClickListener {
            finish()
        }

        val deleteReportButton = findViewById<Button>(R.id.DeleteReportButton)
        deleteReportButton.setOnClickListener {
            deleteReport()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    private fun updateReport() {
        val newName = reportNameEditText.text.toString()
        val newDescription = descriptionEditText.text.toString()
        val newLocation = locationSpinner.selectedItem.toString()
        val newStatus = statusSpinner.selectedItem.toString()

        if (newName.isBlank() || newDescription.isBlank() || newLocation.isBlank() || newStatus.isBlank()) {
            Toast.makeText(this, "No deje campos en blanco", Toast.LENGTH_SHORT).show()
            return
        }

        // Search for the report in the database
        val query = database.child("reports").orderByChild("name").equalTo(reportName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (reportSnapshot in snapshot.children) {
                        val report = reportSnapshot.getValue(Report::class.java)
                        if (report != null && report.description == reportDescription &&
                            report.location == reportLocation && report.status == reportStatus) {

                            val updatedReport = Report(newName, newDescription, newLocation, reportImageUrl, newStatus)
                            reportSnapshot.ref.setValue(updatedReport)
                                .addOnSuccessListener {
                                    Toast.makeText(this@ModifyReportActivity, "Reporte modificado", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this@ModifyReportActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                } else {
                    Toast.makeText(this@ModifyReportActivity, "Reporte no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ModifyReportActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteReport() {
        val query = database.child("reports").orderByChild("name").equalTo(reportName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (reportSnapshot in snapshot.children) {
                        val report = reportSnapshot.getValue(Report::class.java)
                        if (report != null && report.description == reportDescription &&
                            report.location == reportLocation && report.status == reportStatus) {

                            reportSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(this@ModifyReportActivity, "Reporte eliminado", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this@ModifyReportActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                } else {
                    Toast.makeText(this@ModifyReportActivity, "Reporte no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ModifyReportActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DISPLAY_NAME), null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val imageName = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        selectedImageNameTextView.text = imageName
                        selectedImageNameTextView.visibility = TextView.VISIBLE
                    }
                }
            }
        }
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i) == myString) {
                return i
            }
        }
        return 0
    }
}
