package com.samedtevin.bagcilarapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.samedtevin.bagcilarapp.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    suspend fun getUser(): User?{
        val uid = firebaseAuth.currentUser!!.uid

        return firestore.collection("users").document(uid).get().await().toObject(User::class.java)
    }
}