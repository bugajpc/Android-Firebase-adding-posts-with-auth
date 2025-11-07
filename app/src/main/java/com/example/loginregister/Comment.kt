package com.example.loginregister

import com.google.firebase.Timestamp

data class Comment(val id: String, val pid: String, val uid: String, val content: String, val date: Timestamp)
