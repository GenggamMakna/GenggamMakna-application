package com.example.genggammakna.ui


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.genggammakna.databinding.ActivityCameraxBinding
class CameraxActivity : AppCompatActivity() {

    companion object{
        private const val TAG ="CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "Camerax Image"
        const val CAMERAX_RESULT = 200
    }

    private lateinit var binding : ActivityCameraxBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imgCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()

        binding.switchCamera.setOnClickListener {
            cameraSelector = if(
                cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
            ) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        binding.takePic.setOnClickListener { takePic() }
    }

    private fun initBinding(){
        binding = ActivityCameraxBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
            preview.setSurfaceProvider(binding.cameraView.surfaceProvider)

            imgCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imgCapture
                )
            } catch (exception: Exception) {
                Toast.makeText(
                    this@CameraxActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exception.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun takePic(){
        val imgCapture = imgCapture ?: return
        val photoFile = createTempFile(application.toString())
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imgCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra(EXTRA_CAMERAX_IMAGE, outputFileResults.savedUri.toString())
                    setResult(CAMERAX_RESULT, intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraxActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exception.message}")
                }
            }
        )
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this){
            override fun onOrientationChanged(orientation: Int) {
                if(orientation == ORIENTATION_UNKNOWN){
                    return
                }

                val rotate = when(orientation){
                    in 45 until  135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imgCapture?.targetRotation = rotate
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }
}