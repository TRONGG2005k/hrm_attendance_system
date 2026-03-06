package com.example.attendance_app

import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.RectF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*

class FaceDetectionAnalyzer(
    private val overlayView: FaceGuideOverlayView,
    private val resultCallback: (Boolean, Face?) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector: FaceDetector

    init {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .enableTracking()
            .build()

        detector = FaceDetection.getClient(options)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        detector.process(image)
            .addOnSuccessListener { faces ->

                if (faces.isEmpty()) {
                    overlayView.updateFaceBounds(null, false)
                    resultCallback(false, null)
                    return@addOnSuccessListener
                }

                val face = faces.first()

                val mappedRect =
                    convertToOverlayRect(face.boundingBox, imageProxy)

                val guide = overlayView.getGuideFrame()

                val isValid =
                    guide.contains(
                        mappedRect.left.toInt(),
                        mappedRect.top.toInt(),
                        mappedRect.right.toInt(),
                        mappedRect.bottom.toInt()
                    )

                overlayView.updateFaceBounds(mappedRect, isValid)

                resultCallback(isValid, face)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    /**
     * ✅ FIXED coordinate mapping
     */
    private fun convertToOverlayRect(
        faceRect: Rect,
        imageProxy: ImageProxy
    ): RectF {

        val rotation = imageProxy.imageInfo.rotationDegrees

        val viewWidth = overlayView.width.toFloat()
        val viewHeight = overlayView.height.toFloat()

        val imageWidth = imageProxy.width.toFloat()
        val imageHeight = imageProxy.height.toFloat()

        val scaleX: Float
        val scaleY: Float

        val rect = RectF(faceRect)

        when (rotation) {

            0 -> {
                scaleX = viewWidth / imageWidth
                scaleY = viewHeight / imageHeight
            }

            90, 270 -> {
                scaleX = viewWidth / imageHeight
                scaleY = viewHeight / imageWidth
            }

            else -> {
                scaleX = viewWidth / imageWidth
                scaleY = viewHeight / imageHeight
            }
        }

        rect.left *= scaleX
        rect.right *= scaleX
        rect.top *= scaleY
        rect.bottom *= scaleY

        /**
         * ✅ FIX FRONT CAMERA MIRROR
         */
        val mirroredLeft = viewWidth - rect.right
        val mirroredRight = viewWidth - rect.left

        rect.left = mirroredLeft
        rect.right = mirroredRight

        return rect
    }
    fun shutdown() {
        detector.close()
    }
}