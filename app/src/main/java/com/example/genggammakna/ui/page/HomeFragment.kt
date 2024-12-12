package com.example.genggammakna.ui.page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.genggammakna.R
import com.example.genggammakna.ui.SibiActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val buttonSibi: Button = view.findViewById(R.id.buttonSibi)
        buttonSibi.setOnClickListener {
            startActivity(Intent(activity, SibiActivity::class.java))
        }
        return view
    }

}