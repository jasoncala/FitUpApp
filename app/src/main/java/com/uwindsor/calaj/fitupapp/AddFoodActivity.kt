package com.uwindsor.calaj.fitupapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class AddFoodActivity : AppCompatActivity() {
    private lateinit var btnCreateNewFood: Button
    private lateinit var btnAddSavedFood: Button
    private lateinit var btnSearchFood: Button
    private lateinit var btnBackToDiary: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        btnCreateNewFood = findViewById(R.id.btnCreateNewFood)
        btnAddSavedFood = findViewById(R.id.btnAddSavedFood)
        btnSearchFood = findViewById(R.id.btnSearchFood)
        btnBackToDiary = findViewById(R.id.btnBackToDiary)

        val meal = intent.getStringExtra("meal")
        val calExtra = intent.getIntExtra("calExtra", 0)
        val carbExtra = intent.getIntExtra("carbExtra", 0)
        val fatExtra = intent.getIntExtra("fatExtra", 0)
        val proteinExtra = intent.getIntExtra("proteinExtra", 0)
        val calorieGoal = intent.getIntExtra("calorieGoal", 0)
        val percentCarbs = intent.getIntExtra("percentCarbs", 0)
        val percentFat = intent.getIntExtra("percentFat", 0)
        val percentProtein = intent.getIntExtra("percentProtein", 0)


        btnCreateNewFood.setOnClickListener {
            val intent = Intent(this, CreateFoodActivity::class.java)
            intent.putExtra("meal", meal)
            intent.putExtra("calExtra", calExtra)
            intent.putExtra("carbExtra", carbExtra)
            intent.putExtra("fatExtra", fatExtra)
            intent.putExtra("proteinExtra", proteinExtra)
            intent.putExtra("calorieGoal", calorieGoal)
            intent.putExtra("percentCarbs", percentCarbs)
            intent.putExtra("percentFat", percentFat)
            intent.putExtra("percentProtein", percentProtein)

            startActivity(intent)
        }

        btnAddSavedFood.setOnClickListener {
            val intent = Intent(this, ArchivedFoodsActivity::class.java)
            intent.putExtra("meal", meal)
            intent.putExtra("calExtra", calExtra)
            intent.putExtra("carbExtra", carbExtra)
            intent.putExtra("fatExtra", fatExtra)
            intent.putExtra("proteinExtra", proteinExtra)
            intent.putExtra("calorieGoal", calorieGoal)
            intent.putExtra("percentCarbs", percentCarbs)
            intent.putExtra("percentFat", percentFat)
            intent.putExtra("percentProtein", percentProtein)

            startActivity(intent)
        }

        btnSearchFood.setOnClickListener {
            val intent = Intent(this, SearchFoodActivity::class.java)
            intent.putExtra("meal", meal)
            intent.putExtra("calExtra", calExtra)
            intent.putExtra("carbExtra", carbExtra)
            intent.putExtra("fatExtra", fatExtra)
            intent.putExtra("proteinExtra", proteinExtra)
            intent.putExtra("calorieGoal", calorieGoal)
            intent.putExtra("percentCarbs", percentCarbs)
            intent.putExtra("percentFat", percentFat)
            intent.putExtra("percentProtein", percentProtein)

            startActivity(intent)
        }

        btnBackToDiary.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}