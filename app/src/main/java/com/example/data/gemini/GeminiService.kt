package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class ExerciseEvaluationResult(
    val exerciseName: String,
    val estimatedMet: Double,
    val activeCaloriesBurned: Double,
    val epocPercentage: Double,
    val epocCaloriesBurned: Double,
    val primaryMuscleGroup: String,
    val secondaryMuscleGroups: String,
    val recoveryDemand: String,
    val isLiveAi: Boolean = true
)

data class FoodInspectionResult(
    val foodName: String,
    val servingSize: String,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val totalFatGrams: Double,
    val healthyUnsaturatedOilsGrams: Double,
    val minerals: List<String>,
    val vitamins: List<String>,
    val recoveryImpact: String,
    val isLiveAi: Boolean = true
)

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (e: Throwable) {
            ""
        }
    }

    suspend fun evaluateExercise(
        exerciseName: String,
        durationMinutes: Int,
        sets: Int,
        reps: Int,
        intensityTier: String,
        userWeightKg: Double,
        userBmi: Double
    ): ExerciseEvaluationResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            Log.d(TAG, "No valid Gemini API key found, executing physiological baseline fallback.")
            return@withContext fallbackExerciseEvaluation(
                exerciseName, durationMinutes, sets, reps, intensityTier, userWeightKg, userBmi
            )
        }

        try {
            val prompt = """
                You are a sports science and metabolic EPOC analyzer.
                Analyze this exercise session:
                Exercise: $exerciseName
                Duration: $durationMinutes minutes
                Sets: $sets
                Reps: $reps
                Intensity Tier: $intensityTier
                User Weight: $userWeightKg kg
                User BMI: $userBmi

                Return ONLY a valid JSON object with EXACTLY this structure:
                {
                  "exerciseName": "$exerciseName",
                  "estimatedMet": <realistic MET number between 3.0 and 14.0>,
                  "activeCaloriesBurned": <MET * $userWeightKg * ($durationMinutes / 60.0) rounded to 1 decimal place>,
                  "epocPercentage": <percentage number between 5.0 and 18.0 based on intensity: 5-7% for low/cardio, 10-12% for hypertrophy, 15-18% for HIIT/heavy compounds>,
                  "epocCaloriesBurned": <activeCaloriesBurned * (epocPercentage / 100.0) rounded to 1 decimal place>,
                  "primaryMuscleGroup": "<primary target muscle group, e.g. Quadriceps, Gluteus Maximus>",
                  "secondaryMuscleGroups": "<secondary stabilizers and synergists, e.g. Hamstrings, Calves, Core / Lower Back>",
                  "recoveryDemand": "<one of: Low, Moderate, High, Extreme>"
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray()
                    partsArray.put(JSONObject().apply { put("text", prompt) })
                    put("parts", partsArray)
                }
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API error code: ${response.code}, falling back to formula.")
                return@withContext fallbackExerciseEvaluation(
                    exerciseName, durationMinutes, sets, reps, intensityTier, userWeightKg, userBmi
                )
            }

            val responseBody = response.body?.string() ?: throw IllegalStateException("Empty response")
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates")
            val content = candidates?.optJSONObject(0)?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            var text = parts?.optJSONObject(0)?.optString("text") ?: ""

            // Clean markdown wraps if any
            text = text.trim()
            if (text.startsWith("```json")) {
                text = text.removePrefix("```json").trim()
            }
            if (text.startsWith("```")) {
                text = text.removePrefix("```").trim()
            }
            if (text.endsWith("```")) {
                text = text.removeSuffix("```").trim()
            }

            val parsed = JSONObject(text)
            val met = parsed.optDouble("estimatedMet", 6.0)
            val activeCal = parsed.optDouble("activeCaloriesBurned", met * userWeightKg * (durationMinutes / 60.0))
            val epocPct = parsed.optDouble("epocPercentage", 11.0)
            val epocCal = parsed.optDouble("epocCaloriesBurned", activeCal * (epocPct / 100.0))
            val muscle = parsed.optString("primaryMuscleGroup", "Core & Full Body")
            val secondary = parsed.optString("secondaryMuscleGroups", "Stabilizers & Synergists")
            val demand = parsed.optString("recoveryDemand", "Moderate")

            ExerciseEvaluationResult(
                exerciseName = parsed.optString("exerciseName", exerciseName),
                estimatedMet = (met * 10.0).roundToInt() / 10.0,
                activeCaloriesBurned = (activeCal * 10.0).roundToInt() / 10.0,
                epocPercentage = (epocPct * 10.0).roundToInt() / 10.0,
                epocCaloriesBurned = (epocCal * 10.0).roundToInt() / 10.0,
                primaryMuscleGroup = muscle,
                secondaryMuscleGroups = secondary,
                recoveryDemand = demand,
                isLiveAi = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API call failed: ${e.message}", e)
            fallbackExerciseEvaluation(
                exerciseName, durationMinutes, sets, reps, intensityTier, userWeightKg, userBmi
            )
        }
    }

    suspend fun inspectFood(description: String): FoodInspectionResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            Log.d(TAG, "No valid Gemini API key found, executing nutritional baseline fallback.")
            return@withContext fallbackFoodInspection(description)
        }

        try {
            val prompt = """
                You are a sports nutritionist and biochemical food inspector.
                Analyze this meal/food description: "$description"

                Return ONLY a valid JSON object with EXACTLY this structure:
                {
                  "foodName": "<concise name of the food>",
                  "servingSize": "<estimated serving size, e.g. 1 plate (350g) or 2 eggs with toast>",
                  "calories": <total estimated calories in kcal>,
                  "proteinGrams": <protein in grams, number>,
                  "carbsGrams": <carbs in grams, number>,
                  "totalFatGrams": <total fat in grams, number>,
                  "healthyUnsaturatedOilsGrams": <healthy unsaturated oils/fats in grams, e.g. from olive oil, avocado, fish, nuts, seeds>,
                  "minerals": ["<key mineral 1>", "<key mineral 2>", "<key mineral 3>"],
                  "vitamins": ["<key vitamin 1>", "<key vitamin 2>", "<key vitamin 3>"],
                  "recoveryImpact": "<concise 1-line note explaining its post-workout biochemical recovery benefit>"
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray()
                    partsArray.put(JSONObject().apply { put("text", prompt) })
                    put("parts", partsArray)
                }
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini food inspector code: ${response.code}, falling back to formula.")
                return@withContext fallbackFoodInspection(description)
            }

            val responseBody = response.body?.string() ?: throw IllegalStateException("Empty response")
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates")
            val content = candidates?.optJSONObject(0)?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            var text = parts?.optJSONObject(0)?.optString("text") ?: ""

            text = text.trim()
            if (text.startsWith("```json")) {
                text = text.removePrefix("```json").trim()
            }
            if (text.startsWith("```")) {
                text = text.removePrefix("```").trim()
            }
            if (text.endsWith("```")) {
                text = text.removeSuffix("```").trim()
            }

            val parsed = JSONObject(text)
            val mineralsList = mutableListOf<String>()
            val mineralsArr = parsed.optJSONArray("minerals")
            if (mineralsArr != null) {
                for (i in 0 until mineralsArr.length()) {
                    mineralsList.add(mineralsArr.optString(i))
                }
            }
            if (mineralsList.isEmpty()) mineralsList.addAll(listOf("Potassium", "Magnesium", "Zinc"))

            val vitaminsList = mutableListOf<String>()
            val vitaminsArr = parsed.optJSONArray("vitamins")
            if (vitaminsArr != null) {
                for (i in 0 until vitaminsArr.length()) {
                    vitaminsList.add(vitaminsArr.optString(i))
                }
            }
            if (vitaminsList.isEmpty()) vitaminsList.addAll(listOf("Vitamin B12", "Vitamin D", "Vitamin E"))

            FoodInspectionResult(
                foodName = parsed.optString("foodName", description),
                servingSize = parsed.optString("servingSize", "1 serving"),
                calories = (parsed.optDouble("calories", 350.0) * 10.0).roundToInt() / 10.0,
                proteinGrams = (parsed.optDouble("proteinGrams", 20.0) * 10.0).roundToInt() / 10.0,
                carbsGrams = (parsed.optDouble("carbsGrams", 30.0) * 10.0).roundToInt() / 10.0,
                totalFatGrams = (parsed.optDouble("totalFatGrams", 12.0) * 10.0).roundToInt() / 10.0,
                healthyUnsaturatedOilsGrams = (parsed.optDouble("healthyUnsaturatedOilsGrams", 8.0) * 10.0).roundToInt() / 10.0,
                minerals = mineralsList,
                vitamins = vitaminsList,
                recoveryImpact = parsed.optString("recoveryImpact", "Supports glycogen replenishing and cellular recovery."),
                isLiveAi = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Gemini food inspection failed: ${e.message}", e)
            fallbackFoodInspection(description)
        }
    }

    // Physiological baseline MET engine fallback
    private fun fallbackExerciseEvaluation(
        exerciseName: String,
        durationMinutes: Int,
        sets: Int,
        reps: Int,
        intensityTier: String,
        userWeightKg: Double,
        userBmi: Double
    ): ExerciseEvaluationResult {
        val lowerName = exerciseName.lowercase()
        var baseMet = 5.5
        var primaryMuscle = "Full Body"
        var secondaryMuscle = "Core & Stabilizers"
        var epocPct = 10.0
        var demand = "Moderate"

        when {
            lowerName.contains("squat") || lowerName.contains("leg press") -> {
                baseMet = 7.5
                primaryMuscle = "Quadriceps, Gluteus Maximus"
                secondaryMuscle = "Hamstrings, Calves, Core / Lower Back"
                epocPct = 14.0
                demand = "High"
            }
            lowerName.contains("deadlift") || lowerName.contains("rdl") -> {
                baseMet = 8.0
                primaryMuscle = "Hamstrings, Spinal Erectors"
                secondaryMuscle = "Gluteus Maximus, Latissimus Dorsi, Forearms"
                epocPct = 16.0
                demand = "Extreme"
            }
            lowerName.contains("bench") || lowerName.contains("chest") || lowerName.contains("pushup") -> {
                baseMet = 6.0
                primaryMuscle = "Pectoralis Major, Anterior Deltoids"
                secondaryMuscle = "Triceps Brachii, Serratus Anterior"
                epocPct = 11.0
                demand = "Moderate"
            }
            lowerName.contains("pull up") || lowerName.contains("row") || lowerName.contains("lat") -> {
                baseMet = 6.8
                primaryMuscle = "Latissimus Dorsi, Rhomboids"
                secondaryMuscle = "Biceps Brachii, Posterior Deltoids, Trapezius"
                epocPct = 12.0
                demand = "High"
            }
            lowerName.contains("burpee") || lowerName.contains("hiit") || lowerName.contains("sprint") || lowerName.contains("circuit") -> {
                baseMet = 11.0
                primaryMuscle = "Cardiovascular, Quadriceps"
                secondaryMuscle = "Core, Pectorals, Calves, Shoulders"
                epocPct = 17.5
                demand = "Extreme"
            }
            lowerName.contains("curl") || lowerName.contains("bicep") || lowerName.contains("tricep") || lowerName.contains("lateral raise") -> {
                baseMet = 4.5
                primaryMuscle = "Biceps Brachii, Deltoids"
                secondaryMuscle = "Brachialis, Forearm Flexors"
                epocPct = 7.0
                demand = "Low"
            }
            lowerName.contains("run") || lowerName.contains("jog") || lowerName.contains("cycling") -> {
                baseMet = 8.5
                primaryMuscle = "Lower Body, Cardiovascular"
                secondaryMuscle = "Gluteus Medius, Core Stabilizers"
                epocPct = 9.0
                demand = "Moderate"
            }
            lowerName.contains("yoga") || lowerName.contains("stretch") || lowerName.contains("walk") -> {
                baseMet = 3.2
                primaryMuscle = "Mobility, Core"
                secondaryMuscle = "Postural Stabilizers, Spinal Flexors"
                epocPct = 5.5
                demand = "Low"
            }
        }

        // Adjust for intensity tier
        when {
            intensityTier.contains("Maximum") || intensityTier.contains("HIIT") -> {
                baseMet *= 1.25
                epocPct = (epocPct * 1.3).coerceIn(14.0, 18.0)
                demand = "Extreme"
            }
            intensityTier.contains("High") || intensityTier.contains("Compounds") -> {
                baseMet *= 1.15
                epocPct = (epocPct * 1.15).coerceIn(12.0, 16.0)
                if (demand != "Extreme") demand = "High"
            }
            intensityTier.contains("Low") || intensityTier.contains("Warmup") -> {
                baseMet *= 0.85
                epocPct = (epocPct * 0.7).coerceIn(5.0, 7.5)
                demand = "Low"
            }
        }

        val activeCal = baseMet * userWeightKg * (durationMinutes / 60.0)
        val epocCal = activeCal * (epocPct / 100.0)

        return ExerciseEvaluationResult(
            exerciseName = exerciseName,
            estimatedMet = (baseMet * 10.0).roundToInt() / 10.0,
            activeCaloriesBurned = (activeCal * 10.0).roundToInt() / 10.0,
            epocPercentage = (epocPct * 10.0).roundToInt() / 10.0,
            epocCaloriesBurned = (epocCal * 10.0).roundToInt() / 10.0,
            primaryMuscleGroup = primaryMuscle,
            secondaryMuscleGroups = secondaryMuscle,
            recoveryDemand = demand,
            isLiveAi = false
        )
    }

    private fun fallbackFoodInspection(description: String): FoodInspectionResult {
        val lower = description.lowercase()
        var cal = 380.0
        var protein = 22.0
        var carbs = 34.0
        var fat = 14.0
        var healthyOils = 9.0
        val minerals = mutableListOf("Potassium", "Magnesium")
        val vitamins = mutableListOf("Vitamin B-Complex", "Vitamin E")
        var impact = "Provides balanced protein and cellular repair micronutrients."

        when {
            lower.contains("egg") -> {
                cal = 280.0
                protein = 20.0
                carbs = 18.0
                fat = 15.0
                healthyOils = 8.5
                minerals.addAll(listOf("Choline", "Selenium", "Zinc"))
                vitamins.addAll(listOf("Vitamin D", "Vitamin B12", "Lutein"))
                impact = "High bioavailability leucine and choline stimulate mTOR pathway for muscle protein synthesis."
            }
            lower.contains("salmon") || lower.contains("fish") -> {
                cal = 420.0
                protein = 38.0
                carbs = 12.0
                fat = 20.0
                healthyOils = 16.0
                minerals.addAll(listOf("Potassium", "Selenium", "Phosphorus"))
                vitamins.addAll(listOf("Vitamin D", "Vitamin B12", "Omega-3 DHA/EPA"))
                impact = "Potent anti-inflammatory marine omega-3s alleviate delayed onset muscle soreness (DOMS)."
            }
            lower.contains("chicken") || lower.contains("turkey") -> {
                cal = 350.0
                protein = 40.0
                carbs = 15.0
                fat = 9.0
                healthyOils = 4.0
                minerals.addAll(listOf("Phosphorus", "Zinc", "Iron"))
                vitamins.addAll(listOf("Niacin (B3)", "Vitamin B6"))
                impact = "Dense complete protein provides branched-chain amino acids for rapid myofibrillar repair."
            }
            lower.contains("olive oil") || lower.contains("avocado") || lower.contains("nut") -> {
                cal = 320.0
                protein = 8.0
                carbs = 14.0
                fat = 28.0
                healthyOils = 24.0
                minerals.addAll(listOf("Magnesium", "Copper"))
                vitamins.addAll(listOf("Vitamin E", "Polyphenols"))
                impact = "Oleic acid and oleocanthal downregulate oxidative stress while facilitating fat-soluble vitamin uptake."
            }
            lower.contains("shake") || lower.contains("whey") || lower.contains("protein") -> {
                cal = 240.0
                protein = 32.0
                carbs = 15.0
                fat = 4.0
                healthyOils = 2.0
                minerals.addAll(listOf("Calcium", "Potassium"))
                vitamins.addAll(listOf("Vitamin B12"))
                impact = "Fast-digesting whey peptide surge optimizes the post-exercise anabolic window."
            }
        }

        return FoodInspectionResult(
            foodName = description.take(40),
            servingSize = "1 standard serving",
            calories = cal,
            proteinGrams = protein,
            carbsGrams = carbs,
            totalFatGrams = fat,
            healthyUnsaturatedOilsGrams = healthyOils,
            minerals = minerals.distinct().take(4),
            vitamins = vitamins.distinct().take(4),
            recoveryImpact = impact,
            isLiveAi = false
        )
    }
}
