package com.samedtevin.bagcilarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samedtevin.bagcilarapp.model.Announcement
import com.samedtevin.bagcilarapp.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (private val announcementRepository: AnnouncementRepository): ViewModel() {

    private var _random = MutableStateFlow<Announcement?>(null)
    val random = _random.asStateFlow()

    fun getRandomAnnouncement(){
        viewModelScope.launch {
            _random.value = announcementRepository.getRandomAnnouncement()
        }
    }
}