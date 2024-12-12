package com.example.genggammakna.auth


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.genggammakna.R
import com.example.genggammakna.databinding.ActivitySignUpBinding
import com.example.genggammakna.repository.ResultState
import com.example.genggammakna.viewmodel.SignUpViewModel


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentViewInitBinding()
        registerBtnViewModel()
        redirectLogin()
    }
    private fun contentViewInitBinding(){
        supportActionBar?.hide()
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun registerBtnViewModel(){
        binding.tvButtonSignUp.setOnClickListener {
            registerUser()
        }
        observeViewModel()
    }
    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.data, Toast.LENGTH_SHORT).show()
                }
                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerUser() {
        binding.apply {
            val firstname = editTextTextNameFirst.text.toString()
            val lastname = editTextTextNameLast.text.toString()
            val email = editTextTextEmailAddress.text.toString()
            val password = editTextTextPassword.text.toString()
            val confirmPassword = editTextTextConfirmPassword.text.toString()

            if (listOf(firstname, lastname, email, password, confirmPassword).all { it.isNotEmpty() }) {
                if (password == confirmPassword) {
                    viewModel.registerUser(firstname, lastname, email, password)
                } else {
                    Toast.makeText(this@SignUpActivity, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@SignUpActivity, "Mohon isi semua kolom pendaftaran", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun redirectLogin(){
        val signInButton = findViewById<Button>(R.id.btnSignInUser)
        signInButton.setOnClickListener {
            finish()
        }
    }
}
