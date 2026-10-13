package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutDay::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["dayId"])]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dayId: Long,
    val exerciseName: String,
    val durationMinutes: Int,
    val sets: Int,
    val reps: Int,
    val workSeconds: Int = 30,
    val restSeconds: Int = 15,
    val weightUsedKg: Double = 0.0,
    val intensityTier: String,
    val estimatedMet: Double,
    val activeCaloriesBurned: Double,
    val epocPercentage: Double,
    val epocCaloriesBurned: Double,
    val primaryMuscleGroup: String,
    val secondaryMuscleGroups: String = "",
    val recoveryDemand: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalCaloriesBurned: Double
        get() = activeCaloriesBurned + epocCaloriesBurned

    val isBodyweight: Boolean
        get() = weightUsedKg <= 0.0

    val weightLabel: String
        get() = if (isBodyweight) "Bodyweight" else "${if (weightUsedKg % 1.0 == 0.0) weightUsedKg.toInt().toString() else "%.1f".format(weightUsedKg)} kg"

    val primaryMusclesList: List<String>
        get() = primaryMuscleGroup.split(",", "/").map { it.trim() }.filter { it.isNotEmpty() }

    val secondaryMusclesList: List<String>
        get() = secondaryMuscleGroups.split(",", "/").map { it.trim() }.filter { it.isNotEmpty() }
}
