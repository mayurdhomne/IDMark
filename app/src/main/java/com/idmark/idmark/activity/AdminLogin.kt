package com.idmark.idmark.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.idmark.R

class AdminLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_login)

        // Find the TextView by its ID
        val tvAdminLogin: TextView = findViewById(R.id.tvuserlogin)
        // Set an OnClickListener on the TextView
        tvAdminLogin.setOnClickListener {
            // Create an Intent to navigate to AdminLogin activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}