package com.example.genggammakna.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.genggammakna.MainActivity
import com.example.genggammakna.R
import com.example.genggammakna.databinding.ActivityLoginBinding
import com.example.genggammakna.preferences.UserPreferences
import com.example.genggammakna.repository.ResultState
import com.example.genggammakna.repository.UserModel
import com.example.genggammakna.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var emailEdt: EdEdtEmail
    private lateinit var edtTxt: EdtTextPassword

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)
        val user = userPreferences.getUser()
        if (user != null) {

            navToMainActivity()
            return
        }
        supportActionBar?.hide()
        enableEdgeToEdge()
        initBinding()
        setView()
        edtText()
        navSignUp()
        loginBtnViewModel()
        observeViewModel()
    }

    private fun initBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun edtText() {
        emailEdt = binding.editTextTextEmailAddress
        edtTxt = binding.editTextTextPassword
    }

    private fun setView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun navSignUp() {
        val signUpButton = findViewById<Button>(R.id.btnSignUp)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginBtnViewModel() {
        binding.tvButtonSignIn.setOnClickListener {
            loginUser()
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, result.data, Toast.LENGTH_SHORT).show()

                    val user = UserModel("Firstname", "Lastname", "Email") // Data user yang diperoleh dari ViewModel
                    saveUserSession(user)
                    navToMainActivity()
                }
                is ResultState.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserSession(user: UserModel?) {
        if (user != null) {
            userPreferences.saveUser(user)
        }
    }

    private fun navToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginUser() {
        binding.apply {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password)
            } else {
                Toast.makeText(this@LoginActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

