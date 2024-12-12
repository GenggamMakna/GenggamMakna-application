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

class SettingsFragment : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val username = "user"

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = "Hi, $username"

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