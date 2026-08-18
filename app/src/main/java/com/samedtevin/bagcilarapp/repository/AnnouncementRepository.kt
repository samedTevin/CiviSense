package com.samedtevin.bagcilarapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.samedtevin.bagcilarapp.model.Announcement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AnnouncementRepository @Inject constructor(private val firestore: FirebaseFirestore){

    suspend fun getAllAnnouncements(): List<Announcement>?{
        val announcementList = firestore.collection("announcements").get().await().documents.mapNotNull { document ->
            document.toObject(Announcement::class.java)?.copy(id = document.id)
        }

        return announcementList
    }

    suspend fun getRandomAnnouncement(): Announcement?{
        val announcements = firestore.collection("announcements").get().await().documents.mapNotNull { document ->
            document.toObject(Announcement::class.java)?.copy(id = document.id)
        }

        return announcements.randomOrNull()
    }

    suspend fun getLatestAnnouncement(): Announcement?{
        val announcement = firestore.collection("announcements").orderBy("createdAt",Query.Direction.DESCENDING).limit(1).get().await().documents.mapNotNull { document ->
            document.toObject(Announcement::class.java)?.copy(id = document.id)
        }

        return announcement[0]
    }
}