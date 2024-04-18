package com.ran.dicodingstory.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.ran.dicodingstory.R
import com.ran.dicodingstory.data.remote.response.Result
import com.ran.dicodingstory.databinding.ActivityCreateBinding
import com.ran.dicodingstory.ui.models.CreateViewModel
import com.ran.dicodingstory.utils.FileHelper
import com.ran.dicodingstory.utils.GeoLocationHelper
import com.ran.dicodingstory.utils.reduceFileImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding
    private var chooseImage : ActivityResultLauncher<String>? = null
    private var imageUri : Uri? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[CreateViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestPermission()

        chooseImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val galleryUri = it
            try{
                binding.ivPreview.setImageURI(galleryUri)
                imageUri = galleryUri
            }catch(e:Exception){
                e.printStackTrace()
            }

        }


        with(binding){
            btnCamera.setOnClickListener {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            btnGallery.setOnClickListener {
                chooseImage?.launch("image/*")
            }
            btnSubmit.setOnClickListener {
                postStory()
            }
        }
    }

    private fun postStory() {
        if(imageUri != null){
            val description = binding.etCaption.text.toString()
            var location : Pair<Double, Double>? = null
            if (binding.cbUseCurrentLocation.isChecked) {
                location = GeoLocationHelper.getCurrentLatLong(this)
            }
            val imageFile = FileHelper.uriToFile(imageUri!!, this).reduceFileImage()
            viewModel.newStory(imageFile, description, location).observe(this) {
                when(it) {
                    is Result.Error-> {
                        showToast(it.error)
                        showLoading(false)
                    }
                    Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        showToast(getString(R.string.story_posted))
                        finish()
                    }
                }
            }
        } else {
            showToast(getString(R.string.please_select_image))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            val uri = FileHelper.getImageUriFromBitmap(this,photo)
            imageUri = uri
            binding.ivPreview.setImageURI(uri)
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this@CreateActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val CAMERA_REQUEST = 1888
    }

}