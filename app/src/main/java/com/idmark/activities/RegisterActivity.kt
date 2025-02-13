package com.idmark.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.idmark.R
import java.util.concurrent.Executor
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText


    companion object {
        private const val REQUEST_FACE_SCAN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<MaterialButton>(R.id.btnSignUp)
        val btnScanFaceID = findViewById<MaterialButton>(R.id.btnScanFaceID)
        val btnScanFingerprintID = findViewById<MaterialButton>(R.id.btnScanFingerprintID)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        // Restore saved form data
        savedInstanceState?.let {
            etFullName.setText(it.getString("FULL_NAME"))
            etEmail.setText(it.getString("EMAIL"))
            etPassword.setText(it.getString("PASSWORD"))
            etConfirmPassword.setText(it.getString("CONFIRM_PASSWORD"))
        }

        btnScanFaceID.setOnClickListener {
            val intent = Intent(this, FacescanActivity::class.java)
            intent.putExtra("FULL_NAME", etFullName.text.toString())
            intent.putExtra("EMAIL", etEmail.text.toString())
            intent.putExtra("PASSWORD", etPassword.text.toString())
            intent.putExtra("CONFIRM_PASSWORD", etConfirmPassword.text.toString())
            startActivityForResult(intent, REQUEST_FACE_SCAN) // Start FacescanActivity for result
        }

        // Initialize Biometric Authentication
        setupBiometricAuth()

        btnScanFingerprintID.setOnClickListener {
            authenticateUser()
        }

        val tvLogin : TextView = findViewById(R.id.tvLogin)
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnRegister.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    // Receive result from FacescanActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FACE_SCAN && resultCode == Activity.RESULT_OK && data != null) {
            etFullName.setText(data.getStringExtra("FULL_NAME"))
            etEmail.setText(data.getStringExtra("EMAIL"))
            etPassword.setText(data.getStringExtra("PASSWORD"))
            etConfirmPassword.setText(data.getStringExtra("CONFIRM_PASSWORD"))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("FULL_NAME", etFullName.text.toString())
        outState.putString("EMAIL", etEmail.text.toString())
        outState.putString("PASSWORD", etPassword.text.toString())
        outState.putString("CONFIRM_PASSWORD", etConfirmPassword.text.toString())
    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "Authentication Successful!", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Authentication Error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Authentication Failed!", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Use your fingerprint to authenticate")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun authenticateUser() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun validateInputs(): Boolean {
        val name = etFullName.text.toString().trim()
        val emailText = etEmail.text.toString().trim()
        val passwordText = etPassword.text.toString()
        val confirmPasswordText = etConfirmPassword.text.toString()

        if (name.isEmpty()) {
            etFullName.error = "Full Name is required"
            etFullName.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            etEmail.error = "Enter a valid email address"
            etEmail.requestFocus()
            return false
        }

        if (!isValidPassword(passwordText)) {
            etPassword.error = "Password must contain at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, and 1 special character."
            etPassword.requestFocus()
            return false
        }

        if (passwordText != confirmPasswordText) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        return passwordPattern.matcher(password).matches()
    }
}
