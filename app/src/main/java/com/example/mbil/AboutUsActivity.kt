package com.example.mbil

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        // FloatingActionButton click listener
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            // Show dialog with contact information
            showContactDialog()
        }
    }

    private fun showContactDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Contact Us")
        builder.setMessage("Hubungi kami di:\n+62 123 456 789")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Close dialog when OK is pressed
        }
        builder.create().show()
    }
}
