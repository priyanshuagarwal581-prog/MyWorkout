package com.example.util

object SafetyAdvisor {
    /**
     * Evaluates whether the requested weight poses an acute injury risk
     * based on user experience level, user bodyweight, and exercise mechanics.
     * Returns a warning message string if overloaded, or null if safe.
     */
    fun evaluateWeightSafety(
        exerciseName: String,
        weightKg: Double,
        userWeightKg: Double,
        fitnessLevel: String
    ): String? {
        if (weightKg <= 0.0) return null

        val name = exerciseName.lowercase()
        val level = fitnessLevel.lowercase()

        val isBeginner = level.contains("beginner")
        val isIntermediate = level.contains("intermediate")

        val isCompound = name.contains("squat") ||
                name.contains("deadlift") ||
                name.contains("bench") ||
                name.contains("press") ||
                name.contains("row") ||
                name.contains("clean") ||
                name.contains("snatch")

        val isDumbbellOrIsolation = name.contains("curl") ||
                name.contains("lateral raise") ||
                name.contains("tricep") ||
                name.contains("fly") ||
                name.contains("dumbbell press") ||
                name.contains("shoulder press") ||
                name.contains("extension")

        val formattedWeight = if (weightKg % 1.0 == 0.0) weightKg.toInt().toString() else "%.1f".format(weightKg)

        if (isBeginner) {
            // Beginner Thresholds
            if (isDumbbellOrIsolation && weightKg >= 20.0) {
                return "⚠️ Heads up: ${formattedWeight} kg may be too heavy for your current experience level on $exerciseName. Focus on strict form and lower load to prevent joint injury."
            }
            if ((name.contains("squat") || name.contains("leg press")) && (weightKg >= 80.0 || weightKg > userWeightKg * 1.1)) {
                return "⚠️ Heads up: ${formattedWeight} kg may be too heavy for your current experience level on $exerciseName. Focus on strict form and lower load to prevent joint injury."
            }
            if (name.contains("deadlift") && (weightKg >= 90.0 || weightKg > userWeightKg * 1.2)) {
                return "⚠️ Heads up: ${formattedWeight} kg may be too heavy for your current experience level on $exerciseName. Focus on strict form and lower load to prevent spinal strain."
            }
            if (name.contains("bench") && (weightKg >= 60.0 || weightKg > userWeightKg * 0.85)) {
                return "⚠️ Heads up: ${formattedWeight} kg may be too heavy for your current experience level on $exerciseName. Focus on strict form and lower load to protect pectoral tendons."
            }
            if (weightKg >= 75.0) {
                return "⚠️ Heads up: ${formattedWeight} kg may be too heavy for your current experience level on $exerciseName. Focus on strict form and lower load to prevent joint injury."
            }
        } else if (isIntermediate) {
            // Intermediate Thresholds
            if (isDumbbellOrIsolation && weightKg >= 38.0) {
                return "⚠️ Caution: ${formattedWeight} kg is near maximum threshold on $exerciseName. Verify joint stabilization and warm up adequately."
            }
            if (weightKg > userWeightKg * 2.0 && weightKg >= 150.0) {
                return "⚠️ Caution: ${formattedWeight} kg exceeds 2x bodyweight. Ensure proper bracing, belt support, and spotter presence."
            }
        }

        return null
    }
}
