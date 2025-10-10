package com.example.loginregister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        val emailEditText: EditText = findViewById(R.id.register_email_editText)
        val passwordEditText: EditText = findViewById(R.id.register_password_editText)
        val registerButton: Button = findViewById(R.id.register_button)
        val goToLoginTextView: TextView = findViewById(R.id.register_goToLoginButton)
        val loadingLayout: ConstraintLayout = findViewById(R.id.loadingLayout)
        val db = Firebase.firestore

        goToLoginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener {
            //start loading
            loadingLayout.isVisible = true
            registerButton.isEnabled = false

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Register", "createUserWithEmail:success")
                        val user = auth.currentUser
                        val dbUser = hashMapOf(
                            "email" to user?.email.toString(),
                            "avatar" to "https://cdn-icons-png.flaticon.com/512/6596/6596121.png",
                            "timestamp" to Timestamp(Date())
                        )

                        db.collection("users").document(user?.uid.toString())
                            .set(dbUser)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    baseContext,
                                    "Hello " + user?.email.toString(),
                                    Toast.LENGTH_SHORT,
                                ).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                                Log.d("database", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("database", "Error writing document", e) }

                    } else {
                        Log.w("Register", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    //stop loading
                    loadingLayout.isVisible = false
                    registerButton.isEnabled = true
                }
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}