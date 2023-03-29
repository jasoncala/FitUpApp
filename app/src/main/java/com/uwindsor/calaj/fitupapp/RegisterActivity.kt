package com.uwindsor.calaj.fitupapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private lateinit var etEmailRg: EditText
    private lateinit var etPasswordRg: EditText
    private lateinit var etPasswordRgRp: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById<Button>(R.id.btnRegister)
        etEmailRg = findViewById<EditText>(R.id.etEmailRg)
        etPasswordRg = findViewById<EditText>(R.id.etPasswordRg)
        etPasswordRgRp = findViewById<EditText>(R.id.etPasswordRgRp)

        val auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            btnRegister.isEnabled = false
            val email = etEmailRg.text.toString()
            val password = etPasswordRg.text.toString()
            val passwordRep = etPasswordRgRp.text.toString()
            if (email.isBlank() || password.isBlank() || passwordRep.isBlank()){
                Toast.makeText(this, "Email/passwords cannot be empty", Toast.LENGTH_SHORT).show()
                btnRegister.isEnabled = true
                return@setOnClickListener
            }
            if (password != passwordRep){
                Toast.makeText(this, "Passwords must be the same", Toast.LENGTH_SHORT).show()
                btnRegister.isEnabled = true
                return@setOnClickListener
            }

            //Firebase create password-based account
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                btnRegister.isEnabled = true
                if (task.isSuccessful) {
                    //Sign in success
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    signUserIn(auth, email, password)
                } else {
                    Log.e("RegisterActivity", "createUserWithEmail failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUserIn(auth: FirebaseAuth, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if(task.isSuccessful){
                goStartProfileActivity()
            } else {
                Log.e("RegisterActivity", "signInWithEmail failed", task.exception)
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goStartProfileActivity(){
        val intent = Intent(this, DiaryActivity::class.java)
        startActivity(intent)
        finish()
    }
}