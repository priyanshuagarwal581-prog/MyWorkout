package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.ExerciseEvaluationResult
import com.example.data.gemini.FoodInspectionResult
import com.example.data.gemini.GeminiService
import com.example.data.local.AppDatabase
import com.example.data.model.Exercise
import com.example.data.model.FoodLog
import com.example.data.model.UserFeedback
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.workoutDao()

    val userProfile: StateFlow<UserProfile> = dao.getUserProfile()
        .combine(flowOf(Unit)) { profile, _ ->
            profile ?: UserProfile(
                heightCm = 175.0,
                weightKg = 72.0,
                age = 26,
                gender = "Male",
                fitnessLevel = "Beginner",
                onboardingCompleted = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile(
                heightCm = 175.0,
                weightKg = 72.0,
                age = 26,
                gender = "Male",
                fitnessLevel = "Beginner",
                onboardingCompleted = false
            )
        )

    val workoutDays: StateFlow<List<WorkoutDay>> = dao.getAllDays()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedDayId = MutableStateFlow<Long?>(null)
    val selectedDayId: StateFlow<Long?> = _selectedDayId.asStateFlow()

    val currentExercises: StateFlow<List<Exercise>> = _selectedDayId
        .flatMapLatest { dayId ->
            if (dayId != null) {
                dao.getExercisesForDay(dayId)
            } else {
                dao.getAllExercises()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val foodLogs: StateFlow<List<FoodLog>> = dao.getAllFoodLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val feedbacks: StateFlow<List<UserFeedback>> = dao.getAllFeedback()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI Loading & Evaluation States
    private val _isEvaluatingExercise = MutableStateFlow(false)
    val isEvaluatingExercise: StateFlow<Boolean> = _isEvaluatingExercise.asStateFlow()

    private val _lastExerciseResult = MutableStateFlow<ExerciseEvaluationResult?>(null)
    val lastExerciseResult: StateFlow<ExerciseEvaluationResult?> = _lastExerciseResult.asStateFlow()

    private val _isInspectingFood = MutableStateFlow(false)
    val isInspectingFood: StateFlow<Boolean> = _isInspectingFood.asStateFlow()

    private val _lastFoodResult = MutableStateFlow<FoodInspectionResult?>(null)
    val lastFoodResult: StateFlow<FoodInspectionResult?> = _lastFoodResult.asStateFlow()

    private val _bannerNotice = MutableStateFlow<String?>(null)
    val bannerNotice: StateFlow<String?> = _bannerNotice.asStateFlow()

    init {
        // Automatically select first day when days load
        viewModelScope.launch {
            workoutDays.collect { days ->
                if (_selectedDayId.value == null && days.isNotEmpty()) {
                    _selectedDayId.value = days.first().id
                }
            }
        }
    }

    fun selectDay(dayId: Long) {
        _selectedDayId.value = dayId
    }

    fun clearBanner() {
        _bannerNotice.value = null
    }

    fun completeOnboarding(
        age: Int,
        weightKg: Double,
        heightCm: Double,
        gender: String,
        fitnessLevel: String
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                id = 1,
                heightCm = heightCm,
                weightKg = weightKg,
                age = age,
                gender = gender,
                fitnessLevel = fitnessLevel,
                onboardingCompleted = true
            )
            dao.saveUserProfile(profile)
            _bannerNotice.value = "Welcome to My Workout! Profile calibrated: $fitnessLevel."
        }
    }

    fun updateProfile(
        heightCm: Double,
        weightKg: Double,
        age: Int,
        gender: String,
        fitnessLevel: String = userProfile.value.fitnessLevel
    ) {
        viewModelScope.launch {
            val updated = UserProfile(
                id = 1,
                heightCm = heightCm,
                weightKg = weightKg,
                age = age,
                gender = gender,
                fitnessLevel = fitnessLevel,
                onboardingCompleted = true
            )
            dao.saveUserProfile(updated)
            _bannerNotice.value = "Profile updated: BMI %.1f (%s) • %s".format(
                updated.bmi,
                updated.bmiCategory.label,
                updated.fitnessLevel
            )
        }
    }

    fun submitFeedback(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            dao.insertFeedback(UserFeedback(feedbackText = text.trim()))
            _bannerNotice.value = "Thank you for helping us improve!"
        }
    }

    fun addWorkoutDay(title: String, focusDescription: String) {
        viewModelScope.launch {
            val count = workoutDays.value.size
            val newId = dao.insertDay(
                WorkoutDay(
                    title = title,
                    focusDescription = focusDescription,
                    sortOrder = count + 1
                )
            )
            _selectedDayId.value = newId
            _bannerNotice.value = "New routine day \"$title\" created!"
        }
    }

    fun deleteWorkoutDay(day: WorkoutDay) {
        viewModelScope.launch {
            dao.deleteDay(day)
            val remaining = workoutDays.value.filter { it.id != day.id }
            _selectedDayId.value = remaining.firstOrNull()?.id
            _bannerNotice.value = "Routine \"${day.title}\" deleted"
        }
    }

    fun evaluateAndAddExercise(
        exerciseName: String,
        durationMinutes: Int,
        sets: Int,
        reps: Int,
        workSeconds: Int = 30,
        restSeconds: Int = 15,
        weightUsedKg: Double = 0.0,
        intensityTier: String,
        notes: String = ""
    ) {
        val targetDayId = _selectedDayId.value ?: workoutDays.value.firstOrNull()?.id
        if (targetDayId == null) {
            _bannerNotice.value = "Please create or select a workout day first."
            return
        }

        val profile = userProfile.value
        _isEvaluatingExercise.value = true

        viewModelScope.launch {
            try {
                val result = GeminiService.evaluateExercise(
                    exerciseName = exerciseName,
                    durationMinutes = durationMinutes,
                    sets = sets,
                    reps = reps,
                    intensityTier = intensityTier,
                    userWeightKg = profile.weightKg,
                    userBmi = profile.bmi
                )

                _lastExerciseResult.value = result

                dao.insertExercise(
                    Exercise(
                        dayId = targetDayId,
                        exerciseName = result.exerciseName,
                        durationMinutes = durationMinutes,
                        sets = sets,
                        reps = reps,
                        workSeconds = workSeconds,
                        restSeconds = restSeconds,
                        weightUsedKg = weightUsedKg,
                        intensityTier = intensityTier,
                        estimatedMet = result.estimatedMet,
                        activeCaloriesBurned = result.activeCaloriesBurned,
                        epocPercentage = result.epocPercentage,
                        epocCaloriesBurned = result.epocCaloriesBurned,
                        primaryMuscleGroup = result.primaryMuscleGroup,
                        secondaryMuscleGroups = result.secondaryMuscleGroups,
                        recoveryDemand = result.recoveryDemand,
                        notes = notes
                    )
                )

                val aiTag = if (result.isLiveAi) "Live Metabolic Engine" else "Baseline Engine"
                val weightTxt = if (weightUsedKg > 0.0) "${weightUsedKg}kg" else "Bodyweight"
                _bannerNotice.value = "Added %s (%s) via %s: %.0f kcal (EPOC +%.0f)".format(
                    result.exerciseName,
                    weightTxt,
                    aiTag,
                    result.activeCaloriesBurned,
                    result.epocCaloriesBurned
                )
            } catch (e: Exception) {
                _bannerNotice.value = "Exercise evaluation failed: ${e.message}"
            } finally {
                _isEvaluatingExercise.value = false
            }
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            dao.deleteExercise(exercise)
            _bannerNotice.value = "Removed ${exercise.exerciseName}"
        }
    }

    fun inspectAndAddFood(foodDescription: String) {
        val targetDayId = _selectedDayId.value ?: 0L
        _isInspectingFood.value = true

        viewModelScope.launch {
            try {
                val result = GeminiService.inspectFood(foodDescription)
                _lastFoodResult.value = result

                dao.insertFoodLog(
                    FoodLog(
                        dayId = targetDayId,
                        foodName = result.foodName,
                        servingSize = result.servingSize,
                        calories = result.calories,
                        proteinGrams = result.proteinGrams,
                        carbsGrams = result.carbsGrams,
                        totalFatGrams = result.totalFatGrams,
                        healthyUnsaturatedOilsGrams = result.healthyUnsaturatedOilsGrams,
                        mineralsCsv = result.minerals.joinToString(", "),
                        vitaminsCsv = result.vitamins.joinToString(", "),
                        recoveryImpact = result.recoveryImpact
                    )
                )

                val aiTag = if (result.isLiveAi) "Live Nutrition Engine" else "Baseline Engine"
                _bannerNotice.value = "Logged %s via %s: %.0f kcal, %.1fg healthy oils".format(
                    result.foodName,
                    aiTag,
                    result.calories,
                    result.healthyUnsaturatedOilsGrams
                )
            } catch (e: Exception) {
                _bannerNotice.value = "Food inspection failed: ${e.message}"
            } finally {
                _isInspectingFood.value = false
            }
        }
    }

    fun deleteFoodLog(foodLog: FoodLog) {
        viewModelScope.launch {
            dao.deleteFoodLog(foodLog)
            _bannerNotice.value = "Removed ${foodLog.foodName} from food log"
        }
    }
}
