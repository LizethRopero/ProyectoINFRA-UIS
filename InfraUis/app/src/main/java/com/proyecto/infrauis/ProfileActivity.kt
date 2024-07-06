package com.proyecto.infrauis

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        database = FirebaseDatabase.getInstance().reference

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

        }

        val profileImage: ImageView = findViewById(R.id.profileImage)
        val profileEmail: TextView = findViewById(R.id.profileEmail)
        val changePasswordButton: Button = findViewById(R.id.changePasswordButton)

        profileEmail.text = currentUser.email

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Contraseña requerida")
            .setPositiveButton("Confirmar") { _, _ ->
                val password = passwordEditText.text.toString()
                changePassword(password)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun changePassword(newPassword: String) {
        currentUser.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

