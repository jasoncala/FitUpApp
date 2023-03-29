package com.uwindsor.calaj.fitupapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class CreateFoodActivity : AppCompatActivity() {
    private lateinit var etFoodName: EditText
    private lateinit var etCalories: EditText
    private lateinit var etCarbs: EditText
    private lateinit var etFat: EditText
    private lateinit var etProtein: EditText
    private lateinit var btnCreate: Button
    private lateinit var btnCancel: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_food)

        // Initialize variables
        etFoodName = findViewById(R.id.etFoodName)
        etCalories = findViewById(R.id.etCalories)
        etCarbs = findViewById(R.id.etCarbs)
        etFat = findViewById(R.id.etFat)
        etProtein = findViewById(R.id.etProtein)
        btnCreate = findViewById(R.id.btnCreate)
        btnCancel = findViewById(R.id.btnCancel)

    }
}