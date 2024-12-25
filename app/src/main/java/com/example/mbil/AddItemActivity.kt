package com.example.mbil

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null // Pastikan imageUri null-safe
    private lateinit var imageView: ImageView

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        // Firebase initialization
        database = FirebaseDatabase.getInstance().reference.child("items")
        storage = FirebaseStorage.getInstance()

        imageView = findViewById(R.id.imageViewItem)

        val buttonSelectImage = findViewById<Button>(R.id.buttonSelectImage)
        val buttonAddItem = findViewById<Button>(R.id.buttonAddItem)

        buttonSelectImage.setOnClickListener { showImageSourceOptions() }
        buttonAddItem.setOnClickListener { addItem() }

        // Camera launcher setup
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentPhotoPath?.let {
                    val file = File(it)
                    imageUri = Uri.fromFile(file)
                    imageView.setImageURI(imageUri)
                }
            }
        }

        // Gallery launcher setup
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageUri = result.data!!.data
                imageView.setImageURI(imageUri)
            }
        }

        // Permission launcher setup
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageSourceOptions() {
        val options = arrayOf("Camera", "Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkCameraPermission()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val file = createImageFile()
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        currentPhotoPath = file.absolutePath

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun uploadImageToStorage(onSuccess: (String) -> Unit) {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image first!", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = storage.reference.child("item_images/${System.currentTimeMillis()}.jpg")
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addItem() {
        val id = findViewById<EditText>(R.id.editTextItemId).text.toString().trim()
        val name = findViewById<EditText>(R.id.editTextItemName).text.toString().trim()
        val description = findViewById<EditText>(R.id.editTextItemDescription).text.toString().trim()
        val priceText = findViewById<EditText>(R.id.editTextItemPrice).text.toString().trim()

        if (id.isEmpty() || name.isEmpty() || description.isEmpty() || priceText.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill out all fields and select an image!", Toast.LENGTH_SHORT).show()
            return
        }

        uploadImageToStorage { imageUrl ->
            val itemData = Item(
                id = id,
                name = name,
                description = description,
                price = priceText, // Menyimpan price sebagai String
                imageUrl = imageUrl
            )
            database.child(id).setValue(itemData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show()
                    clearInputs()
                } else {
                    Toast.makeText(this, "Failed to add item!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearInputs() {
        findViewById<EditText>(R.id.editTextItemId).text.clear()
        findViewById<EditText>(R.id.editTextItemName).text.clear()
        findViewById<EditText>(R.id.editTextItemDescription).text.clear()
        findViewById<EditText>(R.id.editTextItemPrice).text.clear()
        imageView.setImageResource(0)
        imageUri = null
    }
}
