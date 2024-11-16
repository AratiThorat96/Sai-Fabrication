package com.example.saifb

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PackageManager


class UploadImageActivity : AppCompatActivity() {

    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var selectedImage: ImageView

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    private var imageUri: Uri? = null

    // Permissions request code
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        // Initialize views
        btnCamera = findViewById(R.id.btnCamera)
        btnGallery = findViewById(R.id.btnGallery)
        selectedImage = findViewById(R.id.selectedImage)

        // Check for permissions before using Camera or Gallery
        checkPermissions()

        // Button click listeners
        btnCamera.setOnClickListener {
            openCamera()
        }

        btnGallery.setOnClickListener {
            openGallery()
        }
    }

    // Check if required permissions are granted
    private fun checkPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED || storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE), CAMERA_PERMISSION_CODE)
        }
    }

    // Handle the permission results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permissions are required to access Camera or Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    // Open the camera
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResultLauncher.launch(intent)
    }

    // Open the gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(intent)
    }

    // Activity Result API for Camera
    private val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as Bitmap
            selectedImage.setImageBitmap(photo)
            saveImageToFirebase(photo)
        }
    }

    // Activity Result API for Gallery
    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            selectedImage.setImageURI(imageUri)
            uploadImageToFirebase(imageUri)
        }
    }

    // Save image from Camera to Firebase
    private fun saveImageToFirebase(bitmap: Bitmap) {
        val storageRef = FirebaseStorage.getInstance().reference
        val databaseRef = FirebaseDatabase.getInstance().reference

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageRef = storageRef.child("images/$timeStamp.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        imageRef.putBytes(data)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    databaseRef.child("images").push().setValue(uri.toString())
                    Toast.makeText(this, "Image uploaded and saved successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Upload image from Gallery to Firebase
    private fun uploadImageToFirebase(uri: Uri?) {
        val storageRef = FirebaseStorage.getInstance().reference
        val databaseRef = FirebaseDatabase.getInstance().reference

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageRef = storageRef.child("images/$timeStamp.jpg")

        uri?.let {
            imageRef.putFile(it)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        databaseRef.child("images").push().setValue(downloadUri.toString())
                        Toast.makeText(this, "Image uploaded and saved successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
