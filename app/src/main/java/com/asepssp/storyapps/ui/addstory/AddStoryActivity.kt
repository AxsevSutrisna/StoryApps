package com.asepssp.storyapps.ui.addstory

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.asepssp.storyapps.R
import com.asepssp.storyapps.data.database.repository.Result
import com.asepssp.storyapps.databinding.ActivityAddStroryBinding
import com.asepssp.storyapps.pref.ViewModelFactory
import com.asepssp.storyapps.pref.getImageUri
import com.asepssp.storyapps.pref.reduceFileImage
import com.asepssp.storyapps.pref.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStroryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    @SuppressLint("UnsupportedChromeOsCameraSystemFeature")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStroryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted(Manifest.permission.CAMERA)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            binding.btnCamera.isEnabled = false
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        actionButton()
        setupBar()
    }

    private fun actionButton() {
        binding.btnGallery.setOnClickListener { actionGallery() }
        binding.btnCamera.setOnClickListener { actionCamera() }
        binding.btnUpload.setOnClickListener { uploadStoryWithLocation() }
        binding.checkbox.setOnClickListener {
            if (!allPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION) && !allPermissionsGranted(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                requestPermissionLocation.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                isToast(getString(R.string.accept_add_location))
            }
        }
    }

    private val requestPermissionLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            }

            else -> {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        handlePermissionResult(isGranted)
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            Toast.makeText(this, getText(R.string.permission_accept), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getText(R.string.permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionsGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun actionGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageSelection(uri)
    }

    private fun imageSelection(uri: Uri?) {
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun actionCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        cameraResult(isSuccess)
    }

    private fun cameraResult(isSuccess: Boolean) {
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imgPreviewPhoto.setImageURI(it)
        }
    }

    private fun uploadStoryWithLocation() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edtDescription.text.toString()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                uploadStory(imageFile, description)
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val isChecked = binding.checkbox.isChecked
                    val lat = if (isChecked) location.latitude else null
                    val lon = if (isChecked) location.longitude else null
                    uploadStory(imageFile, description, lat, lon)
                }
            }
            isLoading(true)
        } ?: isToast(getString(R.string.empty_image_warning))
    }

    private fun uploadStory(
        imageFile: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null
    ) {
        viewModel.uploadStory(imageFile, description, lat, lon).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        isToast(result.data.message)
                        isLoading(false)
                        finish()
                    }

                    is Result.Error -> {
                        isToast(result.error)
                        isLoading(false)
                    }
                }
            }
        }
    }


    private fun isLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBar() {
        supportActionBar?.apply {
            title = getString(R.string.bar_add_story_title)
            setDisplayHomeAsUpEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#FEC10E")))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}