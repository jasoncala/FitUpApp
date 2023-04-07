package com.uwindsor.calaj.fitupapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

private const val BASE_URL = "https://api.edamam.com/api/food-database/v2/"
//change the above
private const val API_ID = "610d654e"
private const val API_KEY = "1495799b531bb675f21235f46630111a"
private var FOOD = "chicken"
class SearchFoodActivity : AppCompatActivity() {
    private lateinit var rvSearchFoods: RecyclerView
    private lateinit var etSearchFood: EditText
    private lateinit var btnSearchFood: Button
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_food)

        rvSearchFoods = findViewById(R.id.rvSearchFoods)
        etSearchFood = findViewById(R.id.etSearchFood)
        btnSearchFood = findViewById(R.id.btnSearchFood)

        val possibleFoods = mutableListOf<FoodItem>()
        val adapter = SearchFoodAdapter(this, possibleFoods){
            // add dialog to make sure they are sure they want to add this food
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Would you like to add this food to your diary?")
            builder.setPositiveButton("Yes") { dialog, which ->
                addFoodToDiary(it)
                finish()
            }
            builder.setNeutralButton("Recipe") { dialog, which ->
                // open link to recipe
                val query = "${it.name} recipes"
                val uriBuilder = Uri.Builder()
                    .scheme("https")
                    .authority("www.google.com")
                    .appendPath("search")
                    .appendQueryParameter("q", query)
                val url = uriBuilder.build().toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
        rvSearchFoods.adapter = adapter
        rvSearchFoods.layoutManager = LinearLayoutManager(this)

        callSearchFoodApi(possibleFoods, adapter)

        btnSearchFood.setOnClickListener {
            FOOD = etSearchFood.text.toString()
            callSearchFoodApi(possibleFoods, adapter)
        }

    }

    private fun callSearchFoodApi(
        possibleFoods: MutableList<FoodItem>,
        adapter: SearchFoodAdapter
    ) {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val foodService = retrofit.create(FoodService::class.java)
        foodService.searchFoods(API_ID, API_KEY, FOOD).enqueue(object:
            Callback<FoodSearchResult> {
            override fun onResponse(call: Call<FoodSearchResult>, response: Response<FoodSearchResult>) {
                Log.i("SearchFoodActivity", "onResponse $response")
                val body = response.body()
                if (body == null){
                    Log.w("SearchFoodActivity", "Did not recieve valid response body from API ... exiting")
                    return
                }
                Log.i("SearchFoodActivity", "onResponse $body")
                val foodList = mutableListOf<FoodItem>()
                // take only first 12 results
                var count = 0
                for (i in body.foods) {
                    if (count == 12) break
                    val curItem = i as MutableMap<String, Any>
                    val foodMap = curItem.get("food") as MutableMap<String, Any>
                    val foodName = foodMap.get("label") as String
                    val foodNutrients = foodMap.get("nutrients") as MutableMap<String, Any>
                    val foodCalories = (foodNutrients.get("ENERC_KCAL") as Double).toInt()
                    val foodFat = (foodNutrients.get("FAT") as Double).toInt()
                    val foodCarbs = (foodNutrients.get("CHOCDF") as Double).toInt()
                    val foodProtein = (foodNutrients.get("PROCNT") as Double).toInt()
                    val foodItem = FoodItem(foodName, foodCalories, foodFat, foodCarbs, foodProtein)
                    foodList.add(foodItem)

                    count+=1
                }
                possibleFoods.clear()
                possibleFoods.addAll(foodList)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<FoodSearchResult>, t: Throwable) {
                Log.i("SearchFoodActivity", "onFailure $t")
            }
        })
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