package com.example.genggammakna.ui.page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.genggammakna.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.genggammakna.databinding.FragmentCameraxBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import com.example.genggammakna.ml.Modelslfinal
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraxFragment : Fragment() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: FragmentCameraxBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera(binding.previewView)
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, { imageProxy ->
                        try {
                            processImageProxy(imageProxy)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            imageProxy.close()
                        }
                    })
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,  // Using viewLifecycleOwner for fragments
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private var lastInferenceTime = 0L
    private val inferenceDelay = 10L

    private val detectedSentence = StringBuilder()
    private val sentenceUpdateDelay = 2000L
    private var lastUpdateTime = 0L

    private val alphabet = ('A'.. 'Z').toList()

    private fun processImageProxy(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastInferenceTime < inferenceDelay) {
            return
        }

        lastInferenceTime = currentTime

        val bitmap = imageProxy.toBitmapCompat() ?: return
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        cameraExecutor.execute {
            val model = Modelslfinal.newInstance(requireContext())
            try {
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(scaledBitmap.toByteBuffer())

                val outputs = model.process(inputFeature0)
                val probabilities = outputs.outputFeature0AsTensorBuffer.floatArray

                val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
                val detectedLetter = if (maxIndex in alphabet.indices) alphabet[maxIndex] else "?"

                requireActivity().runOnUiThread {
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
            }
        }
    }

    private fun ImageProxy.toBitmapCompat(): Bitmap? {
        return try {
            val yBuffer = planes[0].buffer
            val uBuffer = planes[1].buffer
            val vBuffer = planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
            val imageBytes = out.toByteArray()
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Bitmap.toByteBuffer(): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        getPixels(intValues, 0, 224, 0, 0, 224, 224)
        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)
        }
        return byteBuffer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
