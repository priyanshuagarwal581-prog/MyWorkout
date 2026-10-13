package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Exercise
import com.example.data.model.FoodLog
import com.example.data.model.UserFeedback
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutDay
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // Workout Days
    @Query("SELECT * FROM workout_days ORDER BY sortOrder ASC, id ASC")
    fun getAllDays(): Flow<List<WorkoutDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: WorkoutDay): Long

    @Update
    suspend fun updateDay(day: WorkoutDay)

    @Delete
    suspend fun deleteDay(day: WorkoutDay)

    // Exercises
    @Query("SELECT * FROM exercises WHERE dayId = :dayId ORDER BY id ASC")
    fun getExercisesForDay(dayId: Long): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises ORDER BY id ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    // Food Logs
    @Query("SELECT * FROM food_logs ORDER BY loggedAt DESC")
    fun getAllFoodLogs(): Flow<List<FoodLog>>

    @Query("SELECT * FROM food_logs WHERE dayId = :dayId ORDER BY loggedAt DESC")
    fun getFoodLogsForDay(dayId: Long): Flow<List<FoodLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(foodLog: FoodLog): Long

    @Delete
    suspend fun deleteFoodLog(foodLog: FoodLog)

    // User Feedback
    @Query("SELECT * FROM user_feedback ORDER BY submittedAt DESC")
    fun getAllFeedback(): Flow<List<UserFeedback>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: UserFeedback): Long
}
