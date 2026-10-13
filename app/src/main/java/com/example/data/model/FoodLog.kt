package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_logs")
data class FoodLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dayId: Long = 0L,
    val foodName: String,
    val servingSize: String,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val totalFatGrams: Double,
    val healthyUnsaturatedOilsGrams: Double,
    val mineralsCsv: String = "", // Comma-separated
    val vitaminsCsv: String = "", // Comma-separated
    val recoveryImpact: String,
    val loggedAt: Long = System.currentTimeMillis()
) {
    val mineralsList: List<String>
        get() = mineralsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val vitaminsList: List<String>
        get() = vitaminsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
