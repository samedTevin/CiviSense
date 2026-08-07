package com.samedtevin.bagcilarapp.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.myDataStore by preferencesDataStore(name = "user_prefs")

class ApplicationSession(val context: Context){

    companion object{
        val USER_ONBOARDING_KEY = booleanPreferencesKey("USER_ONBOARDING")
    }

    suspend fun saveOnboardingPref(isFinished: Boolean){
        context.myDataStore.edit{
            it[USER_ONBOARDING_KEY] = isFinished
        }
    }

    val userOnboardingFlow : Flow<Boolean> = context.myDataStore.data.map {
        it[USER_ONBOARDING_KEY] ?: false
    }

}