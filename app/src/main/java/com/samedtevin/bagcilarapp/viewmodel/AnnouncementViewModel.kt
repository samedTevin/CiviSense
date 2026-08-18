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
class AnnouncementViewModel @Inject constructor(private val announcementRepository: AnnouncementRepository): ViewModel() {
    private var _announcement = MutableStateFlow<List<Announcement>?>(null)
    val announcement get() = _announcement.asStateFlow()

    private var _latest = MutableStateFlow<Announcement?>(null)
    val latest get() = _latest.asStateFlow()

    fun getAnnouncements(){
        viewModelScope.launch {
            _announcement.value = announcementRepository.getAllAnnouncements()
        }
    }

    fun getLatestAnnouncement(){
        viewModelScope.launch {
            _latest.value = announcementRepository.getLatestAnnouncement()
        }
    }
}