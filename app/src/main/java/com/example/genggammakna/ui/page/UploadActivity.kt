package com.example.genggammakna.ui.page

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.genggammakna.R
import com.example.genggammakna.reftrofit.ApiConfig
import com.example.genggammakna.response.PredictionResponse
import okhttp3.RequestBody
import retrofit2.Response
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {

    private lateinit var imageButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var resultText: TextView
    private lateinit var detectButton: Button
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                imageView.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        supportActionBar?.hide()
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        imageButton = findViewById(R.id.imageButtonSibindo)
        imageView = findViewById(R.id.imageViewDisplay)
        resultText = findViewById(R.id.resultText)
        detectButton = findViewById(R.id.detectButton)
    }

    private fun setupListeners() {
        imageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        detectButton.setOnClickListener {
            if (selectedImageUri != null) {
                detectImage(selectedImageUri!!)
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun detectImage(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            val file = bitmapToFile(bitmap)
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

            lifecycleScope.launch {
                try {
                    val response: Response<PredictionResponse> = ApiConfig.getApiService().predictImage(part)
                    if (response.isSuccessful) {
                        val predictionResponse = response.body()
                        if (predictionResponse != null) {
                            resultText.text = "Confidence: ${predictionResponse.confidence}\nPredicted: ${predictionResponse.predicted_alphabet}"
                        } else {
                            resultText.text = "Error: No response body received"
                        }
                    } else {
                        resultText.text = "Error: ${response.message()}"
                    }
                } catch (e: Exception) {
                    resultText.text = "Failed to connect: ${e.message}"
                }
            }
        } catch (e: Exception) {
            resultText.text = "Failed to process image: ${e.message}"
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(cacheDir, "temp_image.jpg")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        return file
    }
}
