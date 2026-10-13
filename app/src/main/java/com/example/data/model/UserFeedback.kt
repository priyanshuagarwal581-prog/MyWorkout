package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_feedback")
data class UserFeedback(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val feedbackText: String,
    val submittedAt: Long = System.currentTimeMillis()
)
