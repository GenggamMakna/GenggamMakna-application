package com.example.genggammakna.ui.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.genggammakna.R
import com.example.genggammakna.preferences.UserPreferences
import com.example.genggammakna.repository.UserModel

class SettingsFragment : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inisialisasi UserPreferences
        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()

        // Tentukan username
        val username = if (user != null) {
            "${user.firstname} ${user.lastname}"
        } else {
            "User"
        }

        // Set username ke TextView
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = "Hi, $username"

        // Setup tombol
        view.findViewById<View>(R.id.btnProfile).setOnClickListener {
            Toast.makeText(requireContext(), "Open Profile clicked", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnTheme).setOnClickListener {
            Toast.makeText(requireContext(), "Theme clicked", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnSignIn).setOnClickListener {
            Toast.makeText(requireContext(), "Sign In clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
