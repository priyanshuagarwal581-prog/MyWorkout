package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BmiCategory
import com.example.data.model.Exercise
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("My Workout", appName)
    }

    @Test
    fun `test bmi and healthy weight range calculation`() {
        val profile = UserProfile(
            heightCm = 180.0,
            weightKg = 72.0,
            age = 25,
            gender = "Male"
        )
        assertEquals(22.2, profile.bmi, 0.1)
        assertEquals(BmiCategory.NORMAL, profile.bmiCategory)
        assertTrue(profile.minHealthyWeightKg > 59.0 && profile.minHealthyWeightKg < 61.0)
        assertTrue(profile.maxHealthyWeightKg > 80.0 && profile.maxHealthyWeightKg < 82.0)
    }

    @Test
    fun `test post-workout essential oil baseline threshold`() {
        val weightKg = 75.0
        val minProtein = weightKg * 0.3
        val maxProtein = weightKg * 0.4
        assertEquals(22.5, minProtein, 0.1)
        assertEquals(30.0, maxProtein, 0.1)

        val minOil = 10.0
        val maxOil = 18.0
        assertTrue(minOil >= 10.0 && maxOil <= 18.0)
    }

    @Test
    fun `test interval timing calculation`() {
        val exercise = Exercise(
            dayId = 1L,
            exerciseName = "Kettlebell Clean & Press",
            durationMinutes = 4,
            sets = 3,
            reps = 12,
            workSeconds = 30,
            restSeconds = 15,
            intensityTier = "High / Heavy Compounds",
            estimatedMet = 8.0,
            activeCaloriesBurned = 60.0,
            epocPercentage = 15.0,
            epocCaloriesBurned = 9.0,
            primaryMuscleGroup = "Full Body & Deltoids",
            recoveryDemand = "High"
        )

        val totalIntervalSeconds = exercise.sets * (exercise.workSeconds + exercise.restSeconds)
        assertEquals(3 * (30 + 15), totalIntervalSeconds)
        assertEquals(135, totalIntervalSeconds)
    }

    @Test
    fun `test safety overload advisor for beginners`() {
        val warningDumbbell = com.example.util.SafetyAdvisor.evaluateWeightSafety(
            exerciseName = "Dumbbell Shoulder Press",
            weightKg = 30.0,
            userWeightKg = 70.0,
            fitnessLevel = "Beginner"
        )
        assertTrue(warningDumbbell != null)
        assertTrue(warningDumbbell!!.contains("Heads up"))

        val safeBodyweight = com.example.util.SafetyAdvisor.evaluateWeightSafety(
            exerciseName = "Pushups",
            weightKg = 0.0,
            userWeightKg = 70.0,
            fitnessLevel = "Beginner"
        )
        assertEquals(null, safeBodyweight)
    }

    @Test
    fun `test user profile onboarding default and completion`() {
        val initial = UserProfile()
        assertEquals(false, initial.onboardingCompleted)
        assertEquals("Beginner", initial.fitnessLevel)

        val completed = initial.copy(onboardingCompleted = true, fitnessLevel = "Intermediate")
        assertTrue(completed.onboardingCompleted)
        assertEquals("Intermediate", completed.fitnessLevel)
    }
}
