package com.cs407.googomap

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // Simulate loading process
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the HomeActivity after the loading
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finish LoadingActivity so the user can't go back to it
        }, 4000) // Show loading page for 34seconds
    }
}
