package com.samedtevin.bagcilarapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(): ViewModel() {

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    fun updateDescription(text: String){
        _description.value = text
    }

}