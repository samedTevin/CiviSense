package com.samedtevin.bagcilarapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.samedtevin.bagcilarapp.model.Announcement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AnnouncementRepository @Inject constructor(private val firestore: FirebaseFirestore){

    suspend fun getAllAnnouncements(){
        firestore.collection("announcements").get().await().documents.mapNotNull { document ->
            document.toObject(Announcement::class.java)?.copy(id = document.id)
        }
    }

    suspend fun getRandomAnnouncement(): Announcement? {
        val announcements = firestore.collection("announcements").get().await().documents.mapNotNull { document ->
            document.toObject(Announcement::class.java)?.copy(id = document.id)
        }

        return announcements.randomOrNull()
    }
}