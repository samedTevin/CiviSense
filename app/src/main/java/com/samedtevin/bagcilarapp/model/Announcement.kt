package com.samedtevin.bagcilarapp.model

import com.google.firebase.Timestamp

data class Announcement(val id: String = "", val title: String = "", val description: String = "",  val imageUrl: String = "", val createdAt: Timestamp? = null)
