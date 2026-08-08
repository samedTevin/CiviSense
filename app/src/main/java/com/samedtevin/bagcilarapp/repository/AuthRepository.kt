package com.samedtevin.bagcilarapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.samedtevin.bagcilarapp.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firebaseFirestore: FirebaseFirestore) {


    suspend fun saveUser(uid: String, user: User){
        firebaseFirestore.collection("users").document(uid).set(user).await()
    }

    suspend fun register(email: String, password: String){
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    fun getUserUid(): String{
        return firebaseAuth.currentUser!!.uid
    }

    suspend fun verifyEmail(){
        if(firebaseAuth.currentUser != null){
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
        }
    }

    suspend fun loginUser(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun reloadUser(){
        firebaseAuth.currentUser?.reload()?.await()
    }

    fun isEmailVerified(): Boolean{
        return firebaseAuth.currentUser?.isEmailVerified == true
    }

    suspend fun loginAsAnon(){
        val user = firebaseAuth.currentUser
        if(user != null){
            firebaseAuth.signOut()
        }
        firebaseAuth.signInAnonymously().await()
    }

    suspend fun resetPassword(email: String){
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    suspend fun changeEmail(){
        val user = firebaseAuth.currentUser
        if(user != null){
            user.delete().await()
            firebaseAuth.signOut()
        }
    }
}