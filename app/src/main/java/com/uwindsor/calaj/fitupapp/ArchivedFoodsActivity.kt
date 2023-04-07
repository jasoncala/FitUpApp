package com.uwindsor.calaj.fitupapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class ArchivedFoodsActivity : AppCompatActivity() {
    private lateinit var rvArchivedFoods: RecyclerView
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archived_foods)

        rvArchivedFoods = findViewById(R.id.rvArchivedFoods)

        val archivedFoods = mutableListOf<FoodItem>()
        val adapter = ArchivedFoodsAdapter(this, archivedFoods) {
            // add dialog to make sure they are sure they want to add this food
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Would you like to add this food to your diary?")
            builder.setPositiveButton("Yes") { dialog, which ->
                addFoodToDiary(it)
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
        rvArchivedFoods.adapter = adapter
        rvArchivedFoods.layoutManager = LinearLayoutManager(this)

        val userID = FirebaseAuth.getInstance().uid
        val document = userID?.let { Firebase.firestore.collection("archived-foods").document(it)}

        GlobalScope.launch(Dispatchers.IO){
            val foodDoc = document?.get()?.await()
            if (foodDoc != null){
                val foodData = foodDoc.data as HashMap<String, Any>?
                if (foodData != null){
                    // loop through all items in foodData
                    for (i in foodData){
                        val uniqueFoodData = i.value as HashMap<String, Any>
                        val foodName = uniqueFoodData.get("foodName").toString()
                        val foodCalories = (uniqueFoodData.get("foodCals") as Long).toInt()
                        val foodProtein = (uniqueFoodData.get("foodProtein") as Long).toInt()
                        val foodCarbs = (uniqueFoodData.get("foodCarbs") as Long).toInt()
                        val foodFat = (uniqueFoodData.get("foodFat") as Long).toInt()

                        val foodItem = FoodItem(name=foodName, calories=foodCalories, protein=foodProtein, carbs=foodCarbs, fat=foodFat)
                        archivedFoods.add(foodItem)
                    }
                }

                withContext(Dispatchers.Main){
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
    private fun addFoodToDiary(foodItem: FoodItem) {
        firestoreDb = FirebaseFirestore.getInstance()
        val usersReference = firestoreDb.collection("users")
        val userID = FirebaseAuth.getInstance().uid
        val userRef = userID?.let { usersReference.document(it) }

        val calendar = Calendar.getInstance()
        val curDate = "${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(
            Calendar.YEAR)}"

        // calculate new consumed macros
        val calExtra = intent.getIntExtra("calExtra", 0)
        val carbExtra = intent.getIntExtra("carbExtra", 0)
        val fatExtra = intent.getIntExtra("fatExtra", 0)
        val proteinExtra = intent.getIntExtra("proteinExtra", 0)
        val meal = intent.getStringExtra("meal")
        val calorieGoal = intent.getIntExtra("calorieGoal", 0)
        val percentCarbs = intent.getIntExtra("percentCarbs", 0)
        val percentFat = intent.getIntExtra("percentFat", 0)
        val percentProtein = intent.getIntExtra("percentProtein", 0)

        val newCals = calExtra + foodItem.calories
        val newCarbs = carbExtra + foodItem.carbs
        val newFat = fatExtra + foodItem.fat
        val newProtein = proteinExtra + foodItem.protein

        // generate random id for a firebase doc
        val foodID = UUID.randomUUID().toString()
        if (meal != null) {
            val newVals = mutableMapOf<String, Any>(
                "diaries" to mutableMapOf<String, Any>(
                    curDate to mutableMapOf<String, Any>(
                        meal to mutableMapOf<String, Any>(
                            foodID to mutableMapOf<String, Any>(
                                "foodName" to foodItem.name,
                                "foodCals" to foodItem.calories,
                                "foodCarbs" to foodItem.carbs,
                                "foodFat" to foodItem.fat,
                                "foodProtein" to foodItem.protein,
                            )
                        ),
                        "calorieGoal" to calorieGoal,
                        "calorieConsumed" to newCals,
                        "carbsConsumed" to newCarbs,
                        "fatConsumed" to newFat,
                        "proteinConsumed" to newProtein,
                        "percentCarbs" to percentCarbs,
                        "percentFat" to percentFat,
                        "percentProtein" to percentProtein
                    )
                )
            )
            if (userRef != null) {
                userRef.set(newVals, SetOptions.merge())
            }
        }
    }


}