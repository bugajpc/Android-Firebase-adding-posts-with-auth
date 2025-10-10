package com.example.loginregister

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Date

class AddPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val titleEdit: EditText = findViewById(R.id.addPost_titleEditText)
        val descriptionEdit: EditText = findViewById(R.id.addPost_descriptionEditText)
        val addButton: Button = findViewById(R.id.addPost_addButton)
        auth = Firebase.auth
        val db = Firebase.firestore

        addButton.setOnClickListener {
            if(titleEdit.text.isEmpty() or descriptionEdit.text.isEmpty()) return@setOnClickListener

            val post = hashMapOf(
                "uid" to auth.currentUser?.uid.toString(),
                "timestamp" to Timestamp(Date()),
                "title" to titleEdit.text.toString(),
                "description" to descriptionEdit.text.toString(),
                "imgURL" to "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png"
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        baseContext,
                        "Adding post successful",
                        Toast.LENGTH_SHORT,
                    ).show()
                    Log.d("addPost", "DocumentSnapshot written with ID: ${documentReference.id}")
                    finish()
                    recreate()
                }
                .addOnFailureListener { e ->
                    Log.w("AddPost", "Error adding document", e)
                }
        }
    }
}