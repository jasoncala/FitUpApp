package com.uwindsor.calaj.fitupapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StartProfileActivity : AppCompatActivity() {

    private lateinit var firestoreDb: FirebaseFirestore

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etCalorieGoal: EditText
    private lateinit var etPercentProtein: EditText
    private lateinit var etPercentCarbs: EditText
    private lateinit var etPercentFat: EditText
    private lateinit var btnSaveProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_profile)

        // Initialize variables
        etName = findViewById(R.id.etFName)
        etAge = findViewById(R.id.etAge)
        etCalorieGoal = findViewById(R.id.etCalorieGoal)
        etPercentProtein = findViewById(R.id.etPercentProtein)
        etPercentCarbs = findViewById(R.id.etPercentCarbs)
        etPercentFat = findViewById(R.id.etPercentFat)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        btnSaveProfile.setOnClickListener{
            val name = etName.text.toString()
            val age = etAge.text.toString()
            val calorieGoal = etCalorieGoal.text.toString()
            val percentProtein = etPercentProtein.text.toString()
            val percentCarbs = etPercentCarbs.text.toString()
            val percentFat = etPercentFat.text.toString()

            saveProfileToDatabase(name, age, calorieGoal, percentProtein, percentCarbs, percentFat)

            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveProfileToDatabase(name: String, age: String, calorieGoal: String, percentProtein: String, percentCarbs: String, percentFat: String){
        firestoreDb = FirebaseFirestore.getInstance()
        val usersReference = firestoreDb.collection("users")
        val user: MutableMap<String, Any> = HashMap()
        user["name"] = name
        user["age"] = age
        user["calorieGoal"] = calorieGoal
        user["percentProtein"] = percentProtein
        user["percentCarbs"] = percentCarbs
        user["percentFat"] = percentFat

        val userID = FirebaseAuth.getInstance().uid

        if (userID != null) {
            usersReference.document(userID).get()
                .addOnSuccessListener {
                    if (it.exists()){
                        usersReference.document(userID).update("name", user["name"])
                        usersReference.document(userID).update("age", user["age"])
                        usersReference.document(userID).update("calorieGoal", user["calorieGoal"])
                        usersReference.document(userID).update("percentProtein", user["percentProtein"])
                        usersReference.document(userID).update("percentCarbs", user["percentCarbs"])
                        usersReference.document(userID).update("percentFat", user["percentFat"])
                    } else {
                        usersReference.document(userID).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this@StartProfileActivity, "record added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                Toast.makeText(this@StartProfileActivity, "failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {

                }
        }
    }
}