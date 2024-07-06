package com.proyecto.infrauis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class ReportActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null
    private lateinit var selectedImageNameTextView: TextView
    private lateinit var locationSpinner: Spinner
    private lateinit var location: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // Inicializa Firebase
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val reportButton = findViewById<ImageButton>(R.id.reportButton)
        val viewReportButton = findViewById<ImageButton>(R.id.viewReportButton)
        val profileButton = findViewById<ImageButton>(R.id.profileButton)
        val blueFilter = android.graphics.PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP)
        val handler = Handler(Looper.getMainLooper())

        homeButton.setOnClickListener {
            homeButton.colorFilter = blueFilter
            handler.postDelayed({ homeButton.clearColorFilter() }, 100)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        reportButton.setOnClickListener {
            reportButton.colorFilter = blueFilter
            handler.postDelayed({ reportButton.clearColorFilter() }, 100)
        }
        viewReportButton.setOnClickListener {
            viewReportButton.colorFilter = blueFilter
            handler.postDelayed({ viewReportButton.clearColorFilter() }, 100)
            val intent = Intent(this, ViewReportActivity::class.java)
            startActivity(intent)
        }
        profileButton.setOnClickListener {
            profileButton.colorFilter = blueFilter
            handler.postDelayed({ profileButton.clearColorFilter() }, 100)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val attachPhotoButton = findViewById<Button>(R.id.attachPhotoButton)
        selectedImageNameTextView = findViewById(R.id.selectedImageNameTextView)
        attachPhotoButton.setOnClickListener {
            selectImage()
        }

        locationSpinner = findViewById(R.id.locationSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.ubicaciones_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        }
        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                location = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Otro manejo si es necesario
            }
        }

        val sendReportButton = findViewById<Button>(R.id.sendReportButton)
        sendReportButton.setOnClickListener {
            if (selectedImageUri != null) {
                uploadImage()
            } else {
                sendReport(null)
            }
        }

        // Configura los botones de las regiones del mapa
        //val region1Button = findViewById<ImageButton>(R.id.region1Button)
        //val region2Button = findViewById<ImageButton>(R.id.region2Button)
        //val region3Button = findViewById<ImageButton>(R.id.region3Button)

        //region1Button.setOnClickListener {
            //locationSpinner.setSelection(1)
        //}
        //region2Button.setOnClickListener {
            //locationSpinner.setSelection(2)
        //}
        //region3Button.setOnClickListener {
            //locationSpinner.setSelection(3)
        //}
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    private fun uploadImage() {
        val imageUri = selectedImageUri ?: return
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                    sendReport(uri.toString())
                }
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendReport(imageUrl: String?) {
        val reportNameEditText = findViewById<EditText>(R.id.reportNameEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)

        val reportName = reportNameEditText.text.toString()
        val description = descriptionEditText.text.toString()

        if (reportName.isBlank() || description.isBlank() || location.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val report = Report(reportName, description, location, imageUrl, "Pendiente")

        database.child("reports").push().setValue(report)
            .addOnSuccessListener {
                Toast.makeText(this, "Reporte enviado exitosamente", Toast.LENGTH_SHORT).show()
                // Limpiar campos después de enviar
                reportNameEditText.text.clear()
                descriptionEditText.text.clear()
                selectedImageUri = null
                selectedImageNameTextView.text = ""
                selectedImageNameTextView.visibility = TextView.GONE
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
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
}
