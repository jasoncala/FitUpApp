package com.uwindsor.calaj.fitupapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.reflect.typeOf

private const val TAG = "DiaryActivity"

class DiaryActivity : AppCompatActivity() {
    private lateinit var tvCalorieGoal: TextView
    private lateinit var tvCalLeftVal: TextView
    private lateinit var pbCalorieLeft: ProgressBar
    private lateinit var tv_cals_consumed_pb: TextView
    private lateinit var tvCarbsVal: TextView
    private lateinit var tvFatVal: TextView
    private lateinit var tvProteinVal: TextView
    private lateinit var tvCarbPercent: TextView
    private lateinit var tvFatPercent: TextView
    private lateinit var tvProteinPercent: TextView
    private lateinit var tvCarbGoal: TextView
    private lateinit var tvFatGoal: TextView
    private lateinit var tvProteinGoal: TextView
    private lateinit var tvCalorieGoalPb: TextView
    private lateinit var pbProteinProg: ProgressBar
    private lateinit var pbCarbsProg: ProgressBar
    private lateinit var pbFatProg: ProgressBar

    private lateinit var tvBreakfastProtein: TextView
    private lateinit var tvBreakfastCarbs: TextView
    private lateinit var tvBreakfastFat: TextView
    private lateinit var tvLunchProtein: TextView
    private lateinit var tvLunchCarbs: TextView
    private lateinit var tvLunchFat: TextView
    private lateinit var tvDinnerProtein: TextView
    private lateinit var tvDinnerCarbs: TextView
    private lateinit var tvDinnerFat: TextView

    private lateinit var rvBreakfast: RecyclerView
    private lateinit var rvLunch: RecyclerView
    private lateinit var rvDinner: RecyclerView

    private lateinit var btnAddBreakfast: Button
    private lateinit var btnAddLunch: Button
    private lateinit var btnAddDinner: Button

    private lateinit var firestoreDb: FirebaseFirestore

    private var curGlobalCalories = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // Initialize variables
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal)
        tvCalLeftVal = findViewById(R.id.tvCalLeftVal)
        pbCalorieLeft = findViewById(R.id.pbCalorieLeft)
        tv_cals_consumed_pb = findViewById(R.id.tv_cals_consumed_pb)
        tvCarbsVal = findViewById(R.id.tvCarbsVal)
        tvFatVal = findViewById(R.id.tvFatVal)
        tvProteinVal = findViewById(R.id.tvProteinVal)
        tvCarbPercent = findViewById(R.id.tvCarbsPercentTitle)
        tvFatPercent = findViewById(R.id.tvFatsPercentTitle)
        tvProteinPercent = findViewById(R.id.tvProteinPercentTitle)
        tvCarbGoal = findViewById(R.id.tvCarbGoal)
        tvFatGoal = findViewById(R.id.tvFatGoal)
        tvProteinGoal = findViewById(R.id.tvProteinGoal)
        tvCalorieGoalPb = findViewById(R.id.tvCalorieGoalPb)
        pbProteinProg = findViewById(R.id.pbProteinProg)
        pbCarbsProg = findViewById(R.id.pbCarbsProg)
        pbFatProg = findViewById(R.id.pbFatProg)

        tvBreakfastProtein = findViewById(R.id.tvBreakfastProtein)
        tvBreakfastCarbs = findViewById(R.id.tvBreakfastCarbs)
        tvBreakfastFat = findViewById(R.id.tvBreakfastFat)
        tvLunchProtein = findViewById(R.id.tvLunchProtein)
        tvLunchCarbs = findViewById(R.id.tvLunchCarbs)
        tvLunchFat = findViewById(R.id.tvLunchFat)
        tvDinnerProtein = findViewById(R.id.tvDinnerProtein)
        tvDinnerCarbs = findViewById(R.id.tvDinnerCarbs)
        tvDinnerFat = findViewById(R.id.tvDinnerFat)

        rvBreakfast = findViewById(R.id.rvBreakfast)
        rvLunch = findViewById(R.id.rvLunch)
        rvDinner = findViewById(R.id.rvDinner)

        btnAddBreakfast = findViewById(R.id.btnAddBreakfast)
        btnAddLunch = findViewById(R.id.btnAddLunch)
        btnAddDinner = findViewById(R.id.btnAddDinner)

        // Set up button listeners
        btnAddBreakfast.setOnClickListener{
            val intent = Intent(this, AddFoodActivity::class.java)
            val CaloriesExtra = tv_cals_consumed_pb.text.toString().toInt()
            val CarbsExtra = (tvCarbsVal.text.toString().split("g")[0]).toInt()
            val FatExtra = (tvFatVal.text.toString().split("g")[0]).toInt()
            val ProteinExtra = (tvProteinVal.text.toString().split("g")[0]).toInt()
            val CalorieGoalExtra = tvCalorieGoal.text.toString().toInt()
            val PercentCarbsExtra = (tvCarbPercent.text.toString().split("%")[0]).toInt()
            val PercentFatExtra = (tvFatPercent.text.toString().split("%")[0]).toInt()
            val PercentProteinExtra = (tvProteinPercent.text.toString().split("%")[0]).toInt()
            val BreakfastProteinExtra = (tvBreakfastProtein.text.toString().split("g")[0]).toInt()
            val BreakfastCarbsExtra = (tvBreakfastCarbs.text.toString().split("g")[0]).toInt()
            val BreakfastFatExtra = (tvBreakfastFat.text.toString().split("g")[0]).toInt()

            intent.putExtra("meal", "breakfast")
            intent.putExtra("calExtra", CaloriesExtra)
            intent.putExtra("carbExtra", CarbsExtra)
            intent.putExtra("fatExtra", FatExtra)
            intent.putExtra("proteinExtra", ProteinExtra)
            intent.putExtra("calorieGoal", CalorieGoalExtra)
            intent.putExtra("percentCarbs", PercentCarbsExtra)
            intent.putExtra("percentFat", PercentFatExtra)
            intent.putExtra("percentProtein", PercentProteinExtra)
            intent.putExtra("breakfastProtein", BreakfastProteinExtra)
            intent.putExtra("breakfastCarbs", BreakfastCarbsExtra)
            intent.putExtra("breakfastFat", BreakfastFatExtra)
            startActivity(intent)
        }

        btnAddLunch.setOnClickListener{
            val intent = Intent(this, AddFoodActivity::class.java)
            val CaloriesExtra = tv_cals_consumed_pb.text.toString().toInt()
            val CarbsExtra = (tvCarbsVal.text.toString().split("g")[0]).toInt()
            val FatExtra = (tvFatVal.text.toString().split("g")[0]).toInt()
            val ProteinExtra = (tvProteinVal.text.toString().split("g")[0]).toInt()
            val CalorieGoalExtra = tvCalorieGoal.text.toString().toInt()
            val PercentCarbsExtra = (tvCarbPercent.text.toString().split("%")[0]).toInt()
            val PercentFatExtra = (tvFatPercent.text.toString().split("%")[0]).toInt()
            val PercentProteinExtra = (tvProteinPercent.text.toString().split("%")[0]).toInt()
            val LunchProteinExtra = (tvLunchProtein.text.toString().split("g")[0]).toInt()
            val LunchCarbsExtra = (tvLunchCarbs.text.toString().split("g")[0]).toInt()
            val LunchFatExtra = (tvLunchFat.text.toString().split("g")[0]).toInt()

            intent.putExtra("meal", "lunch")
            intent.putExtra("calExtra", CaloriesExtra)
            intent.putExtra("carbExtra", CarbsExtra)
            intent.putExtra("fatExtra", FatExtra)
            intent.putExtra("proteinExtra", ProteinExtra)
            intent.putExtra("calorieGoal", CalorieGoalExtra)
            intent.putExtra("percentCarbs", PercentCarbsExtra)
            intent.putExtra("percentFat", PercentFatExtra)
            intent.putExtra("percentProtein", PercentProteinExtra)
            intent.putExtra("lunchProtein", LunchProteinExtra)
            intent.putExtra("lunchCarbs", LunchCarbsExtra)
            intent.putExtra("lunchFat", LunchFatExtra)
            startActivity(intent)
        }

        btnAddDinner.setOnClickListener{
            val intent = Intent(this, AddFoodActivity::class.java)
            val CaloriesExtra = tv_cals_consumed_pb.text.toString().toInt()
            val CarbsExtra = (tvCarbsVal.text.toString().split("g")[0]).toInt()
            val FatExtra = (tvFatVal.text.toString().split("g")[0]).toInt()
            val ProteinExtra = (tvProteinVal.text.toString().split("g")[0]).toInt()
            val CalorieGoalExtra = tvCalorieGoal.text.toString().toInt()
            val PercentCarbsExtra = (tvCarbPercent.text.toString().split("%")[0]).toInt()
            val PercentFatExtra = (tvFatPercent.text.toString().split("%")[0]).toInt()
            val PercentProteinExtra = (tvProteinPercent.text.toString().split("%")[0]).toInt()
            val DinnerProteinExtra = (tvDinnerProtein.text.toString().split("g")[0]).toInt()
            val DinnerCarbsExtra = (tvDinnerCarbs.text.toString().split("g")[0]).toInt()
            val DinnerFatExtra = (tvDinnerFat.text.toString().split("g")[0]).toInt()

            intent.putExtra("meal", "dinner")
            intent.putExtra("calExtra", CaloriesExtra)
            intent.putExtra("carbExtra", CarbsExtra)
            intent.putExtra("fatExtra", FatExtra)
            intent.putExtra("proteinExtra", ProteinExtra)
            intent.putExtra("calorieGoal", CalorieGoalExtra)
            intent.putExtra("percentCarbs", PercentCarbsExtra)
            intent.putExtra("percentFat", PercentFatExtra)
            intent.putExtra("percentProtein", PercentProteinExtra)
            intent.putExtra("dinnerProtein", DinnerProteinExtra)
            intent.putExtra("dinnerCarbs", DinnerCarbsExtra)
            intent.putExtra("dinnerFat", DinnerFatExtra)
            startActivity(intent)
        }

        // Getting data - connect with database
        val userID = FirebaseAuth.getInstance().uid
        firestoreDb = FirebaseFirestore.getInstance()
        val usersReference = firestoreDb.collection("users")
        val userDocument = userID?.let { Firebase.firestore.collection("users").document(it)}
        val userInfo = userID?.let { usersReference.document(it) }

        setupDiaryView(userInfo)
    }

    private fun setupDefaultDiaryDb(){
        firestoreDb = FirebaseFirestore.getInstance()
        val usersReference = firestoreDb.collection("users")
        val userID = FirebaseAuth.getInstance().uid
        val user: MutableMap<String, Any> = HashMap()

    }

    private fun setupDiaryView(userInfo: DocumentReference?) {
        userInfo?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                val data = document.data
                // Setting up data
                if(data != null){
                    val curCalGoal = (data["calorieGoal"] as Long).toInt()
                    val curPercentProtein = (data["percentProtein"] as Long).toDouble()
                    val curPercentCarbs = (data["percentCarbs"] as Long).toDouble()
                    val curPercentFat = (data["percentFat"] as Long).toDouble()

                    // Calculating current macros
                    val (curProteinGoal, curCarbsGoal, curFatGoal) = calculateMacros(curCalGoal, curPercentProtein, curPercentCarbs, curPercentFat)

                    // Set up default data to views
                    tvCalorieGoal.text = curCalGoal.toString()
                    tvCalLeftVal.text = (curCalGoal-0).toString()
                    pbCalorieLeft.max = curCalGoal
                    pbCalorieLeft.progress = curCalGoal
                    tv_cals_consumed_pb.text = "0"
                    curGlobalCalories = 0
                    tvCalorieGoalPb.text = curCalGoal.toString()
                    tvCarbsVal.text = "0g"
                    tvFatVal.text = "0g"
                    tvProteinVal.text = "0g"
                    tvCarbPercent.text = "${curPercentCarbs.toInt()}%"
                    tvFatPercent.text = "${curPercentFat.toInt()}%"
                    tvProteinPercent.text = "${curPercentProtein.toInt()}%"
                    tvCarbGoal.text = "/${curCarbsGoal}g"
                    tvFatGoal.text = "/${curFatGoal}g"
                    tvProteinGoal.text = "/${curProteinGoal}g"
                    tvBreakfastCarbs.text = "0g Carbs"
                    tvBreakfastFat.text = "0g Fat"
                    tvBreakfastProtein.text = "0g Protein"
                    tvLunchCarbs.text = "0g Carbs"
                    tvLunchFat.text = "0g Fat"
                    tvLunchProtein.text = "0g Protein"
                    tvDinnerCarbs.text = "0g Carbs"
                    tvDinnerFat.text = "0g Fat"
                    tvDinnerProtein.text = "0g Protein"
                    pbProteinProg.max = curProteinGoal
                    pbProteinProg.progress = 0
                    pbCarbsProg.max = curCarbsGoal
                    pbCarbsProg.progress = 0
                    pbFatProg.max = curFatGoal
                    pbFatProg.progress = 0

                    val calendar = Calendar.getInstance()
                    val curDate = "${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.YEAR)}"
                    val diaryList = data.get("diaries") as? Map<*, *>
                    val dayDiary = diaryList?.get(curDate) as? Map<*, *>

                    if (dayDiary != null) {
                        val calorieGoal = (dayDiary?.get("calorieGoal") as Long).toInt()
                        val percentCarbs = (dayDiary?.get("percentCarbs") as Long).toDouble()
                        val percentFat = (dayDiary?.get("percentFat") as Long).toDouble()
                        val percentProtein = (dayDiary?.get("percentProtein") as Long).toDouble()
                        val calorieConsumed = (dayDiary?.get("calorieConsumed") as Long).toInt()
                        val carbsConsumed = (dayDiary?.get("carbsConsumed") as Long).toInt()
                        val fatConsumed = (dayDiary?.get("fatConsumed") as Long).toInt()
                        val proteinConsumed = (dayDiary?.get("proteinConsumed") as Long).toInt()

                        val (proteinGoal, carbsGoal, fatGoal) = calculateMacros(
                            calorieGoal,
                            percentProtein,
                            percentCarbs,
                            percentFat
                        )

                        // Set up data to views
                        tvCalorieGoal.text = calorieGoal.toString()
                        tvCalLeftVal.text = (calorieGoal - calorieConsumed).toString()
                        pbCalorieLeft.max = calorieGoal
                        pbCalorieLeft.progress = calorieConsumed
                        tv_cals_consumed_pb.text = calorieConsumed.toString()
                        curGlobalCalories = calorieConsumed
                        tvCalorieGoalPb.text = calorieGoal.toString()
                        tvCarbsVal.text = "${carbsConsumed}g"
                        tvFatVal.text = "${fatConsumed}g"
                        tvProteinVal.text = "${proteinConsumed}g"
                        tvCarbPercent.text = "${percentCarbs.toInt()}%"
                        tvFatPercent.text = "${percentFat.toInt()}%"
                        tvProteinPercent.text = "${percentProtein.toInt()}%"
                        tvCarbGoal.text = "/${carbsGoal}g"
                        tvFatGoal.text = "/${fatGoal}g"
                        tvProteinGoal.text = "/${proteinGoal}g"
                        pbProteinProg.max = proteinGoal
                        pbProteinProg.progress = proteinConsumed
                        pbCarbsProg.max = carbsGoal
                        pbCarbsProg.progress = carbsConsumed
                        pbFatProg.max = fatGoal
                        pbFatProg.progress = fatConsumed

                        // Breakfast adapter
                        val breakfastFoods = mutableListOf<FoodItem>()
                        val breakfastAdapter = FoodListAdapter(this, breakfastFoods)
                        rvBreakfast.adapter = breakfastAdapter
                        rvBreakfast.layoutManager = LinearLayoutManager(this)

                        val breakfastData = dayDiary?.get("breakfast") as? Map<*, *>
                        var totalBreakfastCarbs = 0
                        var totalBreakfastFat = 0
                        var totalBreakfastProtein = 0
                        // loop through all items in breakfastItems
                        if(breakfastData != null) {
                            for ((key, value) in breakfastData!!) {
                                val foodItem = value as? Map<*, *>
                                val name = foodItem?.get("foodName") as String
                                val calories = (foodItem?.get("foodCals") as Long).toInt()
                                val carbs = (foodItem?.get("foodCarbs") as Long).toInt()
                                val fat = (foodItem?.get("foodFat") as Long).toInt()
                                val protein = (foodItem?.get("foodProtein") as Long).toInt()
                                totalBreakfastCarbs += carbs
                                totalBreakfastFat += fat
                                totalBreakfastProtein += protein
                                breakfastFoods.add(FoodItem(name, calories, carbs, fat, protein))
                            }
                        }
                        tvBreakfastCarbs.text = "${totalBreakfastCarbs}g Carbs"
                        tvBreakfastFat.text = "${totalBreakfastFat}g Fat"
                        tvBreakfastProtein.text = "${totalBreakfastProtein}g Protein"

                        // Lunch adapter
                        val lunchFoods = mutableListOf<FoodItem>()
                        val lunchAdapter = FoodListAdapter(this, lunchFoods)
                        rvLunch.adapter = lunchAdapter
                        rvLunch.layoutManager = LinearLayoutManager(this)

                        val lunchData = dayDiary?.get("lunch") as? Map<*, *>
                        var totalLunchCarbs = 0
                        var totalLunchFat = 0
                        var totalLunchProtein = 0
                        // loop through all items in lunchItems
                        if (lunchData != null) {
                            for ((key, value) in lunchData!!) {
                                val foodItem = value as? Map<*, *>
                                val name = foodItem?.get("foodName") as String
                                val calories = (foodItem?.get("foodCals") as Long).toInt()
                                val carbs = (foodItem?.get("foodCarbs") as Long).toInt()
                                val fat = (foodItem?.get("foodFat") as Long).toInt()
                                val protein = (foodItem?.get("foodProtein") as Long).toInt()
                                totalLunchCarbs += carbs
                                totalLunchFat += fat
                                totalLunchProtein += protein
                                lunchFoods.add(FoodItem(name, calories, carbs, fat, protein))
                            }
                        }
                        tvLunchCarbs.text = "${totalLunchCarbs}g Carbs"
                        tvLunchFat.text = "${totalLunchFat}g Fat"
                        tvLunchProtein.text = "${totalLunchProtein}g Protein"

                        // Dinner adapter
                        val dinnerFoods = mutableListOf<FoodItem>()
                        val dinnerAdapter = FoodListAdapter(this, dinnerFoods)
                        rvDinner.adapter = dinnerAdapter
                        rvDinner.layoutManager = LinearLayoutManager(this)

                        val dinnerData = dayDiary?.get("dinner") as? Map<*, *>
                        var totalDinnerCarbs = 0
                        var totalDinnerFat = 0
                        var totalDinnerProtein = 0
                        // loop through all items in dinnerItems
                        if (dinnerData != null) {
                            for ((key, value) in dinnerData!!) {
                                val foodItem = value as? Map<*, *>
                                val name = foodItem?.get("foodName") as String
                                val calories = (foodItem?.get("foodCals") as Long).toInt()
                                val carbs = (foodItem?.get("foodCarbs") as Long).toInt()
                                val fat = (foodItem?.get("foodFat") as Long).toInt()
                                val protein = (foodItem?.get("foodProtein") as Long).toInt()
                                totalDinnerCarbs += carbs
                                totalDinnerFat += fat
                                totalDinnerProtein += protein
                                dinnerFoods.add(FoodItem(name, calories, carbs, fat, protein))
                            }
                        }
                        tvDinnerCarbs.text = "${totalDinnerCarbs}g Carbs"
                        tvDinnerFat.text = "${totalDinnerFat}g Fat"
                        tvDinnerProtein.text = "${totalDinnerProtein}g Protein"
                    }
                }
            }
        }
    }

    private fun calculateMacros(calories: Int, proteinPercent: Double, fatPercent: Double, carbsPercent: Double): Triple<Int, Int, Int> {
        Log.d(TAG, "calculateMacros: ${proteinPercent/100}, $calories, $fatPercent")
        val protein = (calories * (proteinPercent/100)) / 4
        val fat = (calories * (fatPercent/100)) / 9
        val carbs = (calories * (carbsPercent/100)) / 4
        Log.d(TAG, "calculateMacros: $protein, $fat, $carbs")
        return Triple(protein.toInt(), fat.toInt(), carbs.toInt())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_goprofile){
            //val intent = Intent(this, ProfileActivity::class.java)
            //startActivity(intent)
        }
        if (item.itemId == R.id.menu_logout){
            // Log out of Firebase
            FirebaseAuth.getInstance().signOut()
            // Close activity
            finish()
            // Go back to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}