package com.idmark.idmark.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.idmark.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find the TextView by its ID
        val tvAdminLogin: TextView = findViewById(R.id.tvadminlogin)
        // Set an OnClickListener on the TextView
        tvAdminLogin.setOnClickListener {
            // Create an Intent to navigate to AdminLogin activity
            val intent = Intent(this, AdminLogin::class.java)
            startActivity(intent)
        }
    }
}