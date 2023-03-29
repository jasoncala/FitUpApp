package com.uwindsor.calaj.fitupapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

private const val TAG = "DiaryActivity"

class DiaryActivity : AppCompatActivity() {
    private lateinit var tvCalorieGoal: TextView
    private lateinit var tvCalLeftVal: TextView
    private lateinit var pbCalorieLeft: ProgressBar
    private lateinit var tv_cals_consumed_pb: TextView
    private lateinit var tvCarbsVal: TextView
    private lateinit var tvFatVal: TextView
    private lateinit var tvProteinVal: TextView
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

    private lateinit var rvBreakfast: RecyclerView
    private lateinit var rvLunch: RecyclerView
    private lateinit var rvDinner: RecyclerView

    private lateinit var btnAddBreakfast: Button
    private lateinit var btnAddLunch: Button
    private lateinit var btnAddDinner: Button

    private lateinit var firestoreDb: FirebaseFirestore
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

        rvBreakfast = findViewById(R.id.rvBreakfast)
        rvLunch = findViewById(R.id.rvLunch)
        rvDinner = findViewById(R.id.rvDinner)

        btnAddBreakfast = findViewById(R.id.btnAddBreakfast)
        btnAddLunch = findViewById(R.id.btnAddLunch)
        btnAddDinner = findViewById(R.id.btnAddDinner)

        // Breakfast adapter
        val breakfastFoods = mutableListOf<FoodItem>()
        val breakfastAdapter = FoodListAdapter(this, breakfastFoods)
        rvBreakfast.adapter = breakfastAdapter
        rvBreakfast.layoutManager = LinearLayoutManager(this)

        breakfastFoods.add(FoodItem("Egg", 100, 10, 10, 10))
        breakfastFoods.add(FoodItem("Bacon", 100, 10, 10, 10))

        // Lunch adapter
        val lunchFoods = mutableListOf<FoodItem>()
        val lunchAdapter = FoodListAdapter(this, lunchFoods)
        rvLunch.adapter = lunchAdapter
        rvLunch.layoutManager = LinearLayoutManager(this)

        lunchFoods.add(FoodItem("Egg", 100, 10, 10, 10))
        lunchFoods.add(FoodItem("Bacon", 100, 10, 10, 10))
        lunchFoods.add(FoodItem("Bacon", 100, 10, 10, 10))

        // Dinner adapter
        val dinnerFoods = mutableListOf<FoodItem>()
        val dinnerAdapter = FoodListAdapter(this, dinnerFoods)
        rvDinner.adapter = dinnerAdapter
        rvDinner.layoutManager = LinearLayoutManager(this)

        dinnerFoods.add(FoodItem("Egg", 100, 10, 10, 10))


        // Set up button listeners
        btnAddBreakfast.setOnClickListener{
            breakfastFoods.add(FoodItem("Egg", 500, 1, 12, 3))
            breakfastAdapter.notifyDataSetChanged()
        }

        btnAddLunch.setOnClickListener{
            lunchFoods.add(FoodItem("Egg", 500, 1, 12, 3))
            lunchAdapter.notifyDataSetChanged()
        }

        btnAddDinner.setOnClickListener{
            dinnerFoods.add(FoodItem("Egg", 500, 1, 12, 3))
            dinnerAdapter.notifyDataSetChanged()
        }

        // Getting data - TODO connect with database
        val userID = FirebaseAuth.getInstance().uid
        val usersReference = firestoreDb.collection("users")
        // val userDocument = userID?.let { Firebase.firestore.collection("users").document(it)}
        val userInfo = userID?.let { usersReference.document(it) }

        setupDiaryView(userInfo)



        val calorieGoal = 3000
        val calorieConsumed = 1800
        val proteinGoal = 160
        val proteinConsumed = 100
        val carbsGoal = 160
        val carbsConsumed = 100
        val fatGoal = 160
        val fatConsumed = 100

        // Set up data to views
        tvCalorieGoal.text = calorieGoal.toString()
        tvCalLeftVal.text = (calorieGoal - calorieConsumed).toString()
        pbCalorieLeft.max = calorieGoal
        pbCalorieLeft.progress = calorieConsumed
        tv_cals_consumed_pb.text = calorieConsumed.toString()
        tvCalorieGoalPb.text = calorieGoal.toString()
        tvCarbsVal.text = "${carbsConsumed}g"
        tvFatVal.text = "${fatConsumed}g"
        tvProteinVal.text = "${proteinConsumed}g"
        tvCarbGoal.text = "/${carbsGoal}g"
        tvFatGoal.text = "/${fatGoal}g"
        tvProteinGoal.text = "/${proteinGoal}g"
        pbProteinProg.max = proteinGoal
        pbProteinProg.progress = proteinConsumed
        pbCarbsProg.max = carbsGoal
        pbCarbsProg.progress = carbsConsumed
        pbFatProg.max = fatGoal
        pbFatProg.progress = fatConsumed
        tvBreakfastProtein.text = "${proteinConsumed}g Protein"
        tvBreakfastCarbs.text = "${carbsConsumed}g Carbs"
        tvBreakfastFat.text = "${fatConsumed}g Fat"

    }

    private fun setupDiaryView(userInfo: DocumentReference?) {
        userInfo?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                val data = document.data
                // Setting up data

            }
        }
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