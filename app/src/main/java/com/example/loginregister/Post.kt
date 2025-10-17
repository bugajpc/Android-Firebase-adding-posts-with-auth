package com.example.loginregister

import com.google.firebase.Timestamp

data class Post(val id: String, val uid: String, val title: String, val imgURL: String, val description: String, val date: Timestamp)
