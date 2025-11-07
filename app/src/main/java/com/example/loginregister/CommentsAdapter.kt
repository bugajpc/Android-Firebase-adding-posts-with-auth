package com.example.loginregister

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CommentsAdapter(var comments: MutableList<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    val db = Firebase.firestore
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CommentsViewHolder,
        position: Int
    ) {
        val emailTextView: TextView = holder.itemView.findViewById(R.id.comment_emailTextView)
        val contentTextView: TextView = holder.itemView.findViewById(R.id.comment_contentTextView)

        val docRef = db.collection("users").document(comments[position].uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Comment", "DocumentSnapshot data: ${document.data}")
                    emailTextView.text = document.data!!["email"].toString()
                } else {
                    Log.d("Comment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Comment", "get failed with ", exception)
            }

        contentTextView.text = comments[position].content
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}