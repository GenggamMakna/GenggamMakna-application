package com.example.genggammakna.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.genggammakna.R
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        val nameEditText = findViewById<TextInputEditText>(R.id.editTextTextName)
        val emailEditText = findViewById<TextInputEditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<TextInputEditText>(R.id.editTextTextPassword)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.editTextTextConfirmPassword)
        val signUpButton = findViewById<Button>(R.id.tvButtonSignIn)

        val signInButton = findViewById<Button>(R.id.btnSignIn)
        signInButton.setOnClickListener {
            finish()
        }

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            nameEditText.error = null
            emailEditText.error = null
            passwordEditText.error = null
            confirmPasswordEditText.error = null

            var isValid = true

            if (name.isEmpty()) {
                nameEditText.error = "Nama harus diisi"
                isValid = false
            }
            if (email.isEmpty()) {
                emailEditText.error = "Email harus diisi"
                isValid = false
            } else if (!isValidEmail(email)) {
                emailEditText.error = "Email tidak valid"
                isValid = false
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Password harus diisi"
                isValid = false
            } else if (password.length < 8) {
                passwordEditText.error = "Password minimal 8 karakter"
                isValid = false
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.error = "Konfirmasi password harus diisi"
                isValid = false
            } else if (password != confirmPassword) {
                confirmPasswordEditText.error = "Konfirmasi password tidak sama"
                isValid = false
            }
            if (isValid) {
                Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
