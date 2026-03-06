package com.example.attendance_app

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.attendance_app.network.RetrofitClient
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var overlayView: FaceGuideOverlayView
    private lateinit var statusText: TextView
    private lateinit var captureStatusText: TextView

    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null
    private var faceDetectionAnalyzer: FaceDetectionAnalyzer? = null

    // Auto-capture tracking
    private var isFaceValid = false
    private var faceValidStartTime: Long = 0
    private var hasCaptured = false
    private val handler = Handler(Looper.getMainLooper())
    private val captureRunnable = Runnable { performAutoCapture() }

    // Capture delay (1.5 seconds for stable face)
    private val CAPTURE_DELAY_MS = 2000L

    // Callback for captured image
    var onImageCaptured: ((File) -> Unit)? = null
    var onImageCapturedUri: ((Uri) -> Unit)? = null

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        previewView.implementationMode =
            PreviewView.ImplementationMode.COMPATIBLE
        overlayView = findViewById(R.id.overlayView)
        statusText = findViewById(R.id.statusText)
        captureStatusText = findViewById(R.id.captureStatusText)

        cameraExecutor = Executors.newSingleThreadExecutor()

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Image capture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Face detection analyzer
            faceDetectionAnalyzer = FaceDetectionAnalyzer(overlayView) { isValid, face ->
                handleFaceValidation(isValid, face)
            }

            // Image analysis use case
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor!!, faceDetectionAnalyzer!!)
                }

            // Select front camera
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )

            } catch (exc: Exception) {
                Toast.makeText(this, "Failed to start camera: ${exc.message}", Toast.LENGTH_LONG).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun handleFaceValidation(isValid: Boolean, face: Face?) {
        runOnUiThread {
            when {
                face == null -> {
                    updateStatus("No face detected", false)
                    resetCaptureTimer()
                }
                !isValid && face.trackingId == null -> {
                    updateStatus("Face not fully visible", false)
                    resetCaptureTimer()
                }
                !isValid -> {
                    updateStatus("Position your face in the center", false)
                    resetCaptureTimer()
                }
                hasCaptured -> {
                    // Do nothing, already captured
                }
                !isFaceValid -> {
                    // Face just became valid, start timer
                    isFaceValid = true
                    faceValidStartTime = System.currentTimeMillis()
                    updateStatus("Hold still...", true)
                    startCaptureTimer()
                }
                else -> {
                    // Face is still valid, check if enough time has passed
                    val elapsedTime = System.currentTimeMillis() - faceValidStartTime
                    if (elapsedTime >= CAPTURE_DELAY_MS && !hasCaptured) {
                        // Trigger capture
                        hasCaptured = true
                        handler.post(captureRunnable)
                    } else if (!hasCaptured) {
                        val remainingTime = ((CAPTURE_DELAY_MS - elapsedTime) / 1000f).toInt() + 1
                        updateStatus("Hold still in $remainingTime...", true)
                    }
                }
            }
        }
    }

    private fun updateStatus(message: String, isValid: Boolean) {
        statusText.text = message
        statusText.setTextColor(
            if (isValid) {
                ContextCompat.getColor(this, android.R.color.holo_green_light)
            } else {
                ContextCompat.getColor(this, android.R.color.holo_red_light)
            }
        )
    }

    private fun startCaptureTimer() {
        handler.postDelayed(captureRunnable, CAPTURE_DELAY_MS)
    }

    private fun resetCaptureTimer() {
        handler.removeCallbacks(captureRunnable)
        isFaceValid = false
        faceValidStartTime = 0
    }

    private fun performAutoCapture() {
        if (hasCaptured) return

        val imageCapture = imageCapture ?: return

        // Create temporary file in cache directory
        val photoFile = File(
            cacheDir,
            "face_capture_${System.currentTimeMillis()}.jpg"
        )



        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        baseContext,
                        "Photo capture failed: ${exc.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    hasCaptured = false
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    onPhotoCaptured(photoFile, savedUri)
                }
            }
        )
    }

    private fun onPhotoCaptured(photoFile: File, photoUri: Uri) {
        runOnUiThread {
            // Show uploading message
            captureStatusText.visibility = View.VISIBLE
            captureStatusText.text = "Đang xử lý nhận diện..."

            // Trigger callbacks
            onImageCaptured?.invoke(photoFile)
            onImageCapturedUri?.invoke(photoUri)

            // Log the file path
            android.util.Log.d("FaceCapture", "Image saved to: ${photoFile.absolutePath}")
            android.util.Log.d("FaceCapture", "Image URI: $photoUri")

            // Call API to scan face
            uploadAndScanFace(photoFile)
        }
    }

    private fun uploadAndScanFace(photoFile: File) {
        lifecycleScope.launch {
            try {
                // Create MultipartBody.Part from file
                val requestFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", photoFile.name, requestFile)

                // Call API
                val response = RetrofitClient.attendanceApiService.scanFace(body)

                if (response.isSuccessful) {
                    val attendanceResponse = response.body()
                    attendanceResponse?.let {
                        // Update UI with employee info
                        val employeeInfo = "${it.employeeCode ?: ""} - ${it.employeeName ?: ""}"
                        captureStatusText.text = employeeInfo
                        
                        Toast.makeText(
                            this@MainActivity,
                            it.message ?: "Thành công",
                            Toast.LENGTH_LONG
                        ).show()
                        
                        android.util.Log.d("FaceCapture", "API Response: $it")
                    }
                } else {
                    captureStatusText.text = "Nhận diện thất bại"
                    Toast.makeText(
                        this@MainActivity,
                        "Lỗi: ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                captureStatusText.text = "Lỗi kết nối"
                Toast.makeText(
                    this@MainActivity,
                    "Lỗi: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                android.util.Log.e("FaceCapture", "API Error", e)
            }

            // Reset after 3 seconds to allow new capture
            handler.postDelayed({
                resetCaptureState()
            }, 3000)
        }
    }

    private fun resetCaptureState() {
        hasCaptured = false
        isFaceValid = false
        faceValidStartTime = 0
        captureStatusText.visibility = View.GONE
        overlayView.reset()
        updateStatus("Position your face in the center", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(captureRunnable)
        faceDetectionAnalyzer?.shutdown()
        cameraExecutor?.shutdown()
        try {
            cameraExecutor?.awaitTermination(1000, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            // Ignore
        }
    }

    // Public method to check if capture is completed
    fun isCaptureCompleted(): Boolean = hasCaptured

    // Public method to get the captured file (call this after onImageCaptured callback)
    fun getLastCapturedFile(): File? {
        return cacheDir.listFiles { file ->
            file.name.startsWith("face_capture_") && file.name.endsWith(".jpg")
        }?.maxByOrNull { it.lastModified() }
    }


}
