package com.example.loginregister

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import java.util.Date

class SinglePostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_single_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val postId = intent.getStringExtra("pid")?:""
        auth = Firebase.auth
        val avatar: ImageView = findViewById(R.id.single_avatarImageView)
        val postImage: ImageView = findViewById(R.id.single_postImageImageView)
        val email: TextView = findViewById(R.id.single_emailTextView)
        val date: TextView = findViewById(R.id.single_dateTextView)
        val title: TextView = findViewById(R.id.single_titleTextView)
        val desc: TextView = findViewById(R.id.single_descTextView)
        val favorite: ImageView = findViewById(R.id.single_favoriteImageView)
        val likes: TextView = findViewById(R.id.single_likesTextView)

        val db = Firebase.firestore
        db.collection("posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                val singleDate = document.data!!["timestamp"] as Timestamp
                Log.d("documents", "${document.id} => ${document.data}")
                title.text = document.data!!["title"].toString()
                desc.text = document.data!!["description"].toString()
                date.text = singleDate.toDate().toString()
                Picasso.get().load(document.data!!["imgURL"].toString()).into(postImage)
                //read user info
                db.collection("users").document(document.data!!["uid"].toString())
                    .get()
                    .addOnSuccessListener { document2 ->
                        email.text = document2.data!!["email"].toString()
                        Picasso.get().load(document2.data!!["avatar"].toString()).into(avatar)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("documents", "Error getting documents: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("documents", "Error getting documents: ", exception)
            }
    }
}