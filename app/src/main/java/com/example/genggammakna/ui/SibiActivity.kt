package com.example.genggammakna.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.genggammakna.R

class SibiActivity : AppCompatActivity() {
    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sibi)
        supportActionBar?.hide()
        enableEdgeToEdge()

        //cardA <> cardZ
        val cardIds = ('A'..'Z').map { letter ->
            resources.getIdentifier("card$letter", "id", packageName)
        }
        //a.jpg <> z.jpg
        val drawables = ('A'..'Z').map { letter ->
            resources.getIdentifier(letter.lowercase(), "drawable", packageName)
        }
        //title a <> title z
        val titles = ('A'..'Z').map { "Huruf $it" }

        cardIds.forEachIndexed { index, cardId ->
            val cardView = findViewById<CardView>(cardId)
            cardView?.setOnClickListener {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("title", titles[index])
                intent.putExtra("description", "Ini adalah deskripsi untuk ${titles[index]} dalam SIBI.")
                intent.putExtra("imageResId", drawables[index])
                startActivity(intent)
            }
        }
    }
}