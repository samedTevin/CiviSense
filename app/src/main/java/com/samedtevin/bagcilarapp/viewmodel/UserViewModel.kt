package com.samedtevin.bagcilarapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samedtevin.bagcilarapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.samedtevin.bagcilarapp.model.User
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {
    private var _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun getUser(){
       viewModelScope.launch {
           _user.value = userRepository.getUser()
       }
    }
}