package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Exercise
import com.example.data.model.FoodLog
import com.example.data.model.UserFeedback
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        WorkoutDay::class,
        Exercise::class,
        FoodLog::class,
        UserFeedback::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_workout_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default data in background
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).workoutDao()
                                prepopulateDatabase(dao)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(dao: WorkoutDao) {
            dao.saveUserProfile(
                UserProfile(
                    id = 1,
                    heightCm = 178.0,
                    weightKg = 74.0,
                    age = 26,
                    gender = "Male",
                    fitnessLevel = "Intermediate",
                    onboardingCompleted = true
                )
            )

            val day1Id = dao.insertDay(
                WorkoutDay(
                    title = "Day 1 - Heavy Leg Power",
                    focusDescription = "Posterior chain & quad hypertrophic overload",
                    sortOrder = 1
                )
            )

            val day2Id = dao.insertDay(
                WorkoutDay(
                    title = "Day 2 - Push & Core",
                    focusDescription = "Chest, anterior deltoids & anti-rotational core",
                    sortOrder = 2
                )
            )

            val day3Id = dao.insertDay(
                WorkoutDay(
                    title = "Day 3 - HIIT & Conditioning",
                    focusDescription = "High-velocity anaerobic threshold & VO2 max",
                    sortOrder = 3
                )
            )

            // Seed Day 1 sample exercises
            dao.insertExercise(
                Exercise(
                    dayId = day1Id,
                    exerciseName = "Barbell Back Squat",
                    durationMinutes = 25,
                    sets = 4,
                    reps = 8,
                    workSeconds = 35,
                    restSeconds = 60,
                    weightUsedKg = 70.0,
                    intensityTier = "High / Heavy Compounds",
                    estimatedMet = 7.5,
                    activeCaloriesBurned = 231.0,
                    epocPercentage = 15.0,
                    epocCaloriesBurned = 34.7,
                    primaryMuscleGroup = "Quadriceps, Gluteus Maximus",
                    secondaryMuscleGroups = "Hamstrings, Calves, Core / Lower Back",
                    recoveryDemand = "High",
                    notes = "Explosive concentric phase, 3s eccentric"
                )
            )

            dao.insertExercise(
                Exercise(
                    dayId = day1Id,
                    exerciseName = "Romanian Deadlift",
                    durationMinutes = 20,
                    sets = 4,
                    reps = 10,
                    workSeconds = 30,
                    restSeconds = 45,
                    weightUsedKg = 60.0,
                    intensityTier = "High / Heavy Compounds",
                    estimatedMet = 7.0,
                    activeCaloriesBurned = 172.6,
                    epocPercentage = 14.0,
                    epocCaloriesBurned = 24.2,
                    primaryMuscleGroup = "Hamstrings, Spinal Erectors",
                    secondaryMuscleGroups = "Gluteus Maximus, Lats, Forearms",
                    recoveryDemand = "High",
                    notes = "Hinged hip drive with deep hamstring stretch"
                )
            )

            // Seed Day 2 sample exercise
            dao.insertExercise(
                Exercise(
                    dayId = day2Id,
                    exerciseName = "Incline Dumbbell Press",
                    durationMinutes = 20,
                    sets = 4,
                    reps = 10,
                    workSeconds = 30,
                    restSeconds = 30,
                    weightUsedKg = 22.0,
                    intensityTier = "Moderate / Hypertrophy",
                    estimatedMet = 6.0,
                    activeCaloriesBurned = 148.0,
                    epocPercentage = 11.0,
                    epocCaloriesBurned = 16.3,
                    primaryMuscleGroup = "Upper Pectoralis, Anterior Deltoid",
                    secondaryMuscleGroups = "Triceps Brachii, Serratus Anterior",
                    recoveryDemand = "Moderate",
                    notes = "Constant tension at 30-degree bench angle"
                )
            )

            // Seed sample food log
            dao.insertFoodLog(
                FoodLog(
                    dayId = day1Id,
                    foodName = "Wild Salmon with Extra Virgin Olive Oil & Quinoa",
                    servingSize = "200g salmon, 150g quinoa, 15ml EVOO",
                    calories = 540.0,
                    proteinGrams = 42.0,
                    carbsGrams = 38.0,
                    totalFatGrams = 22.0,
                    healthyUnsaturatedOilsGrams = 16.5,
                    mineralsCsv = "Magnesium, Potassium, Zinc, Selenium",
                    vitaminsCsv = "Vitamin D, Vitamin B12, Vitamin E",
                    recoveryImpact = "Rich in EPA/DHA Omega-3 & Oleocanthal for acute muscle soreness reduction and endocrine support."
                )
            )
        }
    }
}
