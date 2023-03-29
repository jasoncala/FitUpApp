package com.uwindsor.calaj.fitupapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class WelcomeActivity : AppCompatActivity() {
    private lateinit var btnWelcome: Button
    private lateinit var tvWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btnWelcome = findViewById(R.id.btnWelcome)
        tvWelcome = findViewById(R.id.tvWelcome)

        val name = intent.getStringExtra("name")

        tvWelcome.text = "Welcome, $name!"

        btnWelcome.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}