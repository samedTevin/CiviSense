package com.samedtevin.bagcilarapp.model

import android.net.Uri

data class Report(val id: String = "", val userId: String = "", val photoUrls: List<String> = emptyList(), val latitude: Double? = null, val longitude: Double? = null, val category: String = "", val priority: String = "", val aiSummary: String = "", val status: String = "Pending", val createdAt: Long = System.currentTimeMillis())
