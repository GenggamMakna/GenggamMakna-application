package com.example.genggammakna

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.genggammakna.auth.LoginActivity
import com.example.genggammakna.preferences.UserPreferences

class StartScreen : AppCompatActivity() {
    private lateinit var btnStart: Button
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_screen)

        userPreferences = UserPreferences(this)
        val currentUser = userPreferences.getUser()
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        btnStart = findViewById(R.id.btn_start)
        btnStart.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
