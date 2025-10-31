package com.example.loginregister

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class PostsAdapter(val posts: MutableList<Post>) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    inner class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    var auth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return PostsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PostsViewHolder,
        position: Int
    ) {
        val title: TextView = holder.itemView.findViewById(R.id.itemTitle)
        val image: ImageView = holder.itemView.findViewById(R.id.itemImage)
        val description: TextView = holder.itemView.findViewById(R.id.itemDescription)
        val date: TextView = holder.itemView.findViewById(R.id.itemDate)
        val favoriteImage: ImageView = holder.itemView.findViewById(R.id.itemFavoriteImage)
        val counter: TextView = holder.itemView.findViewById(R.id.itemCounterFavorites)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SinglePostActivity::class.java)
            intent.putExtra("pid", posts[position].id)
            holder.itemView.context.startActivity(intent)
        }

        val docRef = db.collection("favorites").document(auth.currentUser?.uid.toString() + ":" + posts[position].id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    favoriteImage.setImageResource(R.drawable.baseline_favorite_24)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        db.collection("favorites")
            .whereEqualTo("pid", posts[position].id)
            .get()
            .addOnSuccessListener { documents ->
                counter.text = documents.size().toString()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }

        favoriteImage.setOnClickListener {
            val favorite = hashMapOf(
                "uid" to auth.currentUser?.uid,
                "pid" to posts[position].id
            )

            val docRef = db.collection("favorites").document(auth.currentUser?.uid.toString() + ":" + posts[position].id)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        db.collection("favorites").document(auth.currentUser?.uid.toString() + ":" + posts[position].id)
                            .delete()
                            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                        favoriteImage.setImageResource(R.drawable.outline_favorite_24)
                    } else {
                        Log.d("TAG", "No such document")
                        db.collection("favorites")
                            .document(auth.currentUser?.uid.toString() + ":" + posts[position].id)
                            .set(favorite)
                            .addOnSuccessListener {
                                Log.d("item", "DocumentSnapshot successfully written!")
                                favoriteImage.setImageResource(R.drawable.baseline_favorite_24)
                            }
                            .addOnFailureListener { e -> Log.w("item", "Error writing document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }

        title.text = posts[position].title
        Picasso.get().load(posts[position].imgURL).into(image)
        description.text = posts[position].description
        date.text = posts[position].date.toDate().toString()
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}