package com.example.genggammakna.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.genggammakna.databinding.ActivityDetailBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import com.example.genggammakna.ml.Modelslfinal
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetailActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var camera: Camera
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize layout and camera setup
        initBinding()
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Retrieve intent data and set to UI components
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imageResId = intent.getIntExtra("imageResId", -1)

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        if (imageResId != -1) {
            binding.ivImage.setImageResource(imageResId)
        }

        // Start camera preview
        startCamera(binding.previewView)
    }

    private fun initBinding() {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                // Pastikan previewView sudah tersedia
                previewView.surfaceProvider
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, { imageProxy ->
                        processImageProxy(imageProxy)
                    })
                }

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    // Inference timing variables
    private var lastInferenceTime = 0L
    private val inferenceDelay = 10L

    // Sentence handling
    private val detectedSentence = StringBuilder()
    private val sentenceUpdateDelay = 2000L
    private var lastUpdateTime = 0L

    private val alphabet = ('A'..'Z').toList()

    private fun processImageProxy(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()

        // Ensure delay between inferences
        if (currentTime - lastInferenceTime < inferenceDelay) {
            imageProxy.close()
            return
        }

        lastInferenceTime = currentTime
        val bitmap = imageProxy.toBitmapCompat()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        cameraExecutor.execute {
            val model = Modelslfinal.newInstance(this)
            try {
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(scaledBitmap.toByteBuffer())

                // Run inference on the model
                val outputs = model.process(inputFeature0)
                val probabilities = outputs.outputFeature0AsTensorBuffer.floatArray

                // Determine the detected letter based on maximum probability
                val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
                val detectedLetter = if (maxIndex in alphabet.indices) alphabet[maxIndex] else "?"

                // Update UI with detected letter and sentence
                runOnUiThread {
                    binding.tvDetectedText.text = detectedLetter.toString()

                    if (detectedLetter != "?") {
                        detectedSentence.append(detectedLetter)
                    }

                    if (currentTime - lastUpdateTime >= sentenceUpdateDelay) {
                        lastUpdateTime = currentTime
                        binding.tvDetectedSentence.text = detectedSentence.toString()

                        if (detectedLetter == " ") {
                            detectedSentence.clear()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                model.close()
                imageProxy.close()
            }
        }
    }

    // Helper function to convert ImageProxy to Bitmap
    private fun ImageProxy.toBitmapCompat(): Bitmap {
        val yuvImage = YuvImage(planes[0].buffer.toByteArray(), ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
    }

    // Helper function to convert ByteBuffer to ByteArray
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }

    // Helper function to convert Bitmap to ByteBuffer for model input
    private fun Bitmap.toByteBuffer(): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        getPixels(intValues, 0, 224, 0, 0, 224, 224)
        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f) // R
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)  // G
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f) // B
        }
        return byteBuffer
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
