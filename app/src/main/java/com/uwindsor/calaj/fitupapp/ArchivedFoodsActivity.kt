package com.uwindsor.calaj.fitupapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ArchivedFoodsActivity : AppCompatActivity() {
    private lateinit var rvArchivedFoods: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archived_foods)

        rvArchivedFoods = findViewById(R.id.rvArchivedFoods)

        val archivedFoods = mutableListOf<FoodItem>()
        val adapter = ArchivedFoodsAdapter(this, archivedFoods)
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
                        Log.d("TESTAGAIN", "${i.key} ${i.value}")
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
}