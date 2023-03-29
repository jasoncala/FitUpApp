package com.uwindsor.calaj.fitupapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.typeOf

class CreateFoodActivity : AppCompatActivity() {
    private lateinit var etFoodName: EditText
    private lateinit var etCalories: EditText
    private lateinit var etCarbs: EditText
    private lateinit var etFat: EditText
    private lateinit var etProtein: EditText
    private lateinit var btnCreate: Button
    private lateinit var btnCancel: Button

    private lateinit var firestoreDb: FirebaseFirestore

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

        val meal = intent.getStringExtra("meal")

        btnCreate.setOnClickListener {
            val foodName = etFoodName.text.toString()
            val calories = etCalories.text.toString()
            val carbs = etCarbs.text.toString()
            val fat = etFat.text.toString()
            val protein = etProtein.text.toString()

            if (meal != null) {
                saveFoodToDatabase(foodName, calories, carbs, fat, protein, meal)
            }
            finish()
        }

    }

    private fun saveFoodToDatabase(foodName: String, calories: String, carbs: String, fat: String, protein: String, meal: String){
        firestoreDb = FirebaseFirestore.getInstance()
        val usersReference = firestoreDb.collection("users")
        val userID = FirebaseAuth.getInstance().uid
        val userRef = userID?.let { usersReference.document(it) }

        val foodItem: MutableMap<String, Any> = HashMap()
        foodItem["foodName"] = foodName
        foodItem["calories"] = calories
        foodItem["carbs"] = carbs
        foodItem["fat"] = fat
        foodItem["protein"] = protein

        val calendar = Calendar.getInstance()
        val curDate = "${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(
            Calendar.YEAR)}"

        // calculate new consumed macros
        //val oldMacros = getFoodMacroTotals()
        val calExtra = intent.getIntExtra("calExtra", 0)
        val carbExtra = intent.getIntExtra("carbExtra", 0)
        val fatExtra = intent.getIntExtra("fatExtra", 0)
        val proteinExtra = intent.getIntExtra("proteinExtra", 0)
        Log.d("TESTER", "allMacros: ${calExtra}, ${carbExtra}, ${fatExtra}, ${proteinExtra}")

        val newCals = calExtra + calories.toInt()
        val newCarbs = carbExtra + carbs.toInt()
        val newFat = fatExtra + fat.toInt()
        val newProtein = proteinExtra + protein.toInt()

        // generate random id for a firebase doc
        val foodID = UUID.randomUUID().toString()
        val newVals = mutableMapOf<String, Any>(
            "diaries" to mutableMapOf<String, Any>(
                curDate to mutableMapOf<String, Any>(
                    meal to mutableMapOf<String, Any>(
                        foodID to mutableMapOf<String, Any>(
                            "foodName" to foodName,
                            "foodCals" to calories.toLong(),
                            "foodCarbs" to carbs.toLong(),
                            "foodFat" to fat.toLong(),
                            "foodProtein" to protein.toLong()
                        )
                    ),
                    "calorieConsumed" to newCals,
                    "carbsConsumed" to newCarbs,
                    "fatConsumed" to newFat,
                    "proteinConsumed" to newProtein
                )
            )

        )
        if (userRef != null) {
            userRef.set(newVals, SetOptions.merge())
        }
    }

    private fun getFoodMacroTotals(): IntArray{
        val foodMacros = IntArray(4)

        val userID = FirebaseAuth.getInstance().uid
        firestoreDb = FirebaseFirestore.getInstance()
        val usersReference = firestoreDb.collection("users")
        val userDocument = userID?.let { Firebase.firestore.collection("users").document(it)}
        val userInfo = userID?.let { usersReference.document(it) }

        userInfo?.get()?.addOnSuccessListener { document ->
            Log.d("TESTING1", "ca")
            if (document != null) {
                val data = document.data
                if(data != null){
                    val calendar = Calendar.getInstance()
                    val curDate = "${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.YEAR)}"
                    val diaryList = data?.get("diaries") as? Map<*, *>
                    val dayDiary = diaryList?.get(curDate) as? Map<*, *>

                    if (dayDiary != null) {
                        val calorieConsumed = (dayDiary?.get("calorieConsumed") as Long).toInt()
                        val carbsConsumed = (dayDiary?.get("carbsConsumed") as Long).toInt()
                        val fatConsumed = (dayDiary?.get("fatConsumed") as Long).toInt()
                        val proteinConsumed = (dayDiary?.get("proteinConsumed") as Long).toInt()

                        Log.d("TESTING", "calorieConsumed: $calorieConsumed")

                        foodMacros[0] = calorieConsumed
                        foodMacros[1] = carbsConsumed
                        foodMacros[2] = fatConsumed
                        foodMacros[3] = proteinConsumed
                    }
                }
            }
        }

        return foodMacros

    }
}