package com.example.genggammakna.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.genggammakna.MainActivity
import com.example.genggammakna.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val lupaPasswordButton = findViewById<Button>(R.id.textView3)
        lupaPasswordButton.setOnClickListener {
            Toast.makeText(this, "Tombol Lupa Password ditekan", Toast.LENGTH_SHORT).show()
        }

        val signUpButton = findViewById<Button>(R.id.btnSignUp)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val signInButton = findViewById<Button>(R.id.tvButtonSignIn)
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            emailEditText.error = null
            passwordEditText.error = null

            var isValid = true

            if (email.isEmpty()) {
                emailEditText.error = "Email tidak boleh kosong"
                isValid = false
            } else if (!isValidEmail(email)) {
                emailEditText.error = "Email tidak valid"
                isValid = false
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Password tidak boleh kosong"
                isValid = false
            } else if (password.length < 8) {
                passwordEditText.error = "Password minimal 8 karakter"
                isValid = false
            }
            if (isValid) {
                Toast.makeText(this, "Sign in berhasil! test", Toast.LENGTH_SHORT).show()
                // val intent = Intent(this, MainActivity::class.java)
                // startActivity(intent)
            }
        }
    }

    // Fungsi untuk memvalidasi email
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
