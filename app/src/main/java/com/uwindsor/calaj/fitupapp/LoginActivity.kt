package com.uwindsor.calaj.fitupapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoReg: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoReg = findViewById(R.id.btnGoReg)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            goDiaryActivity() //If user is already logged in, they don't have to do it again
        }

        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isBlank() || password.isBlank()){
                //Toast.makeText(this, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
                btnLogin.isEnabled = true
                return@setOnClickListener
            }
            //Firebase authentication check
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                btnLogin.isEnabled = true
                if(task.isSuccessful){
                    //Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    goDiaryActivity()
                } else {
                    //Log.e("LoginActivity", "signInWithEmail failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGoReg.setOnClickListener {
            goRegister()
        }



    }

    private fun goDiaryActivity(){
        val intent = Intent(this, DiaryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}