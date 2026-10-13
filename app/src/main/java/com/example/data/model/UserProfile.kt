package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.pow
import kotlin.math.roundToInt

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val heightCm: Double = 175.0,
    val weightKg: Double = 72.0,
    val age: Int = 26,
    val gender: String = "Male",
    val fitnessLevel: String = "Beginner", // "Beginner" | "Intermediate" | "Advanced"
    val onboardingCompleted: Boolean = false
) {
    val bmi: Double
        get() {
            val heightM = heightCm / 100.0
            return if (heightM > 0) {
                val value = weightKg / heightM.pow(2)
                (value * 10.0).roundToInt() / 10.0
            } else 22.0
        }

    val bmiCategory: BmiCategory
        get() = when {
            bmi < 18.5 -> BmiCategory.UNDERWEIGHT
            bmi < 25.0 -> BmiCategory.NORMAL
            bmi < 30.0 -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }

    val minHealthyWeightKg: Double
        get() {
            val heightM = heightCm / 100.0
            return ((18.5 * heightM.pow(2)) * 10.0).roundToInt() / 10.0
        }

    val maxHealthyWeightKg: Double
        get() {
            val heightM = heightCm / 100.0
            return ((24.9 * heightM.pow(2)) * 10.0).roundToInt() / 10.0
        }
}

enum class BmiCategory(val label: String, val rangeDesc: String) {
    UNDERWEIGHT("Underweight", "< 18.5"),
    NORMAL("Normal Weight", "18.5 – 24.9"),
    OVERWEIGHT("Overweight", "25.0 – 29.9"),
    OBESE("Obese", "30.0+")
}
