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
import com.google.firebase.firestore.firestore
import java.util.Date

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imgURLEdit: EditText = findViewById(R.id.editPost_imgURL)
        val titleEdit: EditText = findViewById(R.id.editPost_title)
        val descriptionEdit: EditText = findViewById(R.id.editPost_description)
        val saveButton: Button = findViewById(R.id.editPost_saveButton)

        var postId = intent.getStringExtra("pid")?:""
        var imgURL = intent.getStringExtra("imgURL")?:""
        var description = intent.getStringExtra("description")?:""
        var title = intent.getStringExtra("title")?:""
        var date = intent.getStringExtra("date")?:""
        var uid = intent.getStringExtra("uid")?:""

        val db = Firebase.firestore

        imgURLEdit.setText(imgURL)
        descriptionEdit.setText(description)
        titleEdit.setText(title)

        saveButton.setOnClickListener {
            if(imgURLEdit.text.isEmpty() || titleEdit.text.isEmpty() || descriptionEdit.text.isEmpty()) return@setOnClickListener

            imgURL = imgURLEdit.text.toString()
            title = titleEdit.text.toString()
            description = descriptionEdit.text.toString()

            db.collection("posts").document(postId)
                .update("uid", uid,
                    "title", title,
                    "description", description,
                    "imgURL", imgURL)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        baseContext,
                        "Adding post successful",
                        Toast.LENGTH_SHORT,
                    ).show()
                    Log.d("EditPost", "DocumentSnapshot written with ID: ${postId}")
                    finish()
                    recreate()
                }
                .addOnFailureListener { e ->
                    Log.w("EditPost", "Error editing document", e)
                }
        }
    }
}