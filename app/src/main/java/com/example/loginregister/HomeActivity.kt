package com.example.loginregister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        val logOutButton: Button = findViewById(R.id.home_logoutButton)
        val welcomeTextView: TextView = findViewById(R.id.home_welcomeTextView)
        val addImageView: ImageView = findViewById(R.id.home_addImageView)
        val avatarImage: ImageView = findViewById(R.id.home_avatarImageView)
        val refreshButton: Button = findViewById(R.id.home_refreshButton)
        val recyclerView: RecyclerView = findViewById(R.id.homeRecyclerView)
        val db = Firebase.firestore

        val posts = mutableListOf<Post>()
        val postsAdapter = PostsAdapter(posts)
        recyclerView.adapter = postsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(avatarImage);
        welcomeTextView.text = "Welcome " + auth.currentUser?.email

        val docRef = db.collection("users").document(auth.currentUser?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Avatar", "DocumentSnapshot data: ${document.data!!["avatar"].toString()}")
                    Picasso.get().load(document.data!!["avatar"].toString()).into(avatarImage)
                } else {
                    Log.d("Avatar", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Avatar", "get failed with ", exception)
            }

        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                posts.clear()
                for (document in result) {
                    //Log.d("documents", "${document.id} => ${document.data}")
                    val timestamp = document.data["timestamp"] as Timestamp
                    posts.add(Post(document.id,
                        document.data["uid"].toString(),
                        document.data["title"].toString(),
                        document.data["imgURL"].toString(),
                        document.data["description"].toString(),
                        timestamp))
                }
                postsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("documents", "Error getting documents: ", exception)
            }

        refreshButton.setOnClickListener {
            db.collection("posts")
                .get()
                .addOnSuccessListener { result ->
                    posts.clear()
                    for (document in result) {
                        //Log.d("documents", "${document.id} => ${document.data}")
                        val timestamp = document.data["timestamp"] as Timestamp
                        posts.add(Post(document.id,
                            document.data["uid"].toString(),
                            document.data["title"].toString(),
                            document.data["imgURL"].toString(),
                            document.data["description"].toString(),
                            timestamp))
                    }
                    postsAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("documents", "Error getting documents: ", exception)
                }
        }

        addImageView.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}