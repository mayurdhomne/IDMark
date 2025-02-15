package com.idmark.idmark.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.idmark.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FacescanActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var progressText: TextView
    private lateinit var faceDetector: FaceDetector
    private var progress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facescan)

        progressIndicator = findViewById(R.id.scanningProgress)
        progressText = findViewById(R.id.progressText)

        cameraExecutor = Executors.newSingleThreadExecutor()
        initializeFaceDetector()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                showPermissionDeniedDialog()
            }
        }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(this)
                .setTitle("Camera Permission Needed")
                .setMessage("This app requires camera access to scan your face.")
                .setPositiveButton("Grant") { _, _ -> requestPermissionLauncher.launch(Manifest.permission.CAMERA) }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You have permanently denied the camera permission. Please enable it in settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun initializeFaceDetector() {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        faceDetector = FaceDetection.getClient(options)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraPreview: PreviewView = findViewById(R.id.cameraPreview)

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = cameraPreview.surfaceProvider
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FaceAnalyzer())
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e("FaceScan", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private inner class FaceAnalyzer : ImageAnalysis.Analyzer {
        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image ?: return
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty() && progress < 100) {
                        progress += 10
                        if (progress > 100) progress = 100
                        updateProgress(progress)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FaceScan", "Face detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(value: Int) {
        runOnUiThread {
            val animation = ObjectAnimator.ofInt(progressIndicator, "progress", progressIndicator.progress, value)
            animation.duration = 500 // Smooth animation
            animation.start()
            progressText.text = "$value%"

            if (value == 100) {
                showSuccessBottomSheet()
            }
        }
    }

    private fun showSuccessBottomSheet() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_success, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        bottomSheetDialog.setContentView(dialogView)

        val btnGoToDashboard = dialogView.findViewById<Button>(R.id.btnGoToDashboard)
        btnGoToDashboard.setOnClickListener {
            bottomSheetDialog.dismiss()

            // Prepare Intent to return data
            val resultIntent = Intent()
            resultIntent.putExtra("FULL_NAME", intent.getStringExtra("FULL_NAME"))
            resultIntent.putExtra("EMAIL", intent.getStringExtra("EMAIL"))
            resultIntent.putExtra("PASSWORD", intent.getStringExtra("PASSWORD"))
            resultIntent.putExtra("CONFIRM_PASSWORD", intent.getStringExtra("CONFIRM_PASSWORD"))

            setResult(Activity.RESULT_OK, resultIntent) // Send data back to RegisterActivity
            finish()
        }

        bottomSheetDialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        faceDetector.close()
    }
}
