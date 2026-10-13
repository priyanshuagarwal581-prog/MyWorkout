package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutDay
import com.example.ui.components.CyberSkeletonCard
import com.example.ui.components.DualRingBurnDial
import com.example.ui.components.LiveIntervalTimerModal
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.HotAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime
import com.example.util.SafetyAdvisor

@Composable
fun WorkoutPlannerScreen(
    userProfile: UserProfile,
    workoutDays: List<WorkoutDay>,
    selectedDayId: Long?,
    exercises: List<Exercise>,
    isEvaluatingExercise: Boolean,
    onSelectDay: (Long) -> Unit,
    onAddDay: (title: String, focus: String) -> Unit,
    onDeleteDay: (WorkoutDay) -> Unit,
    onAddExercise: (
        exerciseName: String,
        durationMinutes: Int,
        sets: Int,
        reps: Int,
        workSeconds: Int,
        restSeconds: Int,
        weightUsedKg: Double,
        intensityTier: String,
        notes: String
    ) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDayDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showLiveTimerModal by remember { mutableStateOf(false) }

    // Day totals
    val activeBurnTotal = exercises.sumOf { it.activeCaloriesBurned }
    val epocBurnTotal = exercises.sumOf { it.epocCaloriesBurned }

    val currentDay = workoutDays.firstOrNull { it.id == selectedDayId } ?: workoutDays.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberVoid)
            .padding(horizontal = 16.dp)
            .testTag("workout_planner_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WORKOUT ARCHITECT",
                        color = NeonCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = OrbitronFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Multi-Day Routines & Interval Clocks",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = RajdhaniFontFamily
                    )
                }

                Button(
                    onClick = { showAddDayDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberGlassSurfaceElevated,
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier.testTag("add_day_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Day",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "NEW DAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day Selector Scrollable Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                workoutDays.forEach { day ->
                    val isSelected = day.id == selectedDayId
                    val borderColor = if (isSelected) NeonCyan else CyberGlassBorder
                    val bgColor = if (isSelected) NeonCyan.copy(alpha = 0.15f) else CyberGlassSurface

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { onSelectDay(day.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = day.title,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                fontFamily = OrbitronFontFamily
                            )
                            if (day.focusDescription.isNotEmpty()) {
                                Text(
                                    text = day.focusDescription.take(24),
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = RajdhaniFontFamily
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Day Banner & Delete affordance
            if (currentDay != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentDay.title.uppercase(),
                            color = VividLime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                        if (currentDay.focusDescription.isNotEmpty()) {
                            Text(
                                text = currentDay.focusDescription,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = RajdhaniFontFamily
                            )
                        }
                    }

                    if (workoutDays.size > 1) {
                        IconButton(
                            onClick = { onDeleteDay(currentDay) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Day",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Dual-Ring Metabolic Burn Radial Dial
            DualRingBurnDial(
                activeCalories = activeBurnTotal,
                epocCalories = epocBurnTotal
            )

            Spacer(modifier = Modifier.height(16.dp))

            // START WORKOUT Live Interval Player Button (Prominent Call-To-Action)
            Button(
                onClick = { showLiveTimerModal = true },
                enabled = exercises.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF39FF14),
                    contentColor = CyberVoid,
                    disabledContainerColor = Color(0xFF131D2E),
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(
                        1.dp,
                        if (exercises.isNotEmpty()) Color(0xFF39FF14) else CyberGlassBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("start_workout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Workout",
                    tint = if (exercises.isNotEmpty()) CyberVoid else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (exercises.isNotEmpty()) "⚡ START WORKOUT (LIVE INTERVAL CLOCK)" else "ADD EXERCISES TO START TIMER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Loading Skeleton if analyzing an exercise
            if (isEvaluatingExercise) {
                CyberSkeletonCard(statusMessage = "CALCULATING METABOLIC EPOC BURNOUT...")
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Add Exercise Action Button
            Button(
                onClick = { showAddExerciseDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VividLime,
                    contentColor = CyberVoid
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("analyze_add_exercise_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Calculate Icon",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚡ CALCULATE & ADD EXERCISE",
                    fontWeight = FontWeight.Bold,
                    fontFamily = OrbitronFontFamily,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "DAY ROUTINE EXERCISES (${exercises.size})",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = OrbitronFontFamily,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (exercises.isEmpty() && !isEvaluatingExercise) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberGlassSurface)
                        .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Empty",
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "NO EXERCISES LOGGED FOR THIS DAY",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                        Text(
                            text = "Tap above to calculate metabolic output for any movement",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = RajdhaniFontFamily
                        )
                    }
                }
            }
        }

        items(exercises, key = { it.id }) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onDelete = { onDeleteExercise(exercise) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Day Dialog
    if (showAddDayDialog) {
        var dayTitle by remember { mutableStateOf("") }
        var dayFocus by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDayDialog = false },
            containerColor = CyberGlassSurfaceElevated,
            title = {
                Text(
                    text = "CREATE WORKOUT DAY",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OrbitronFontFamily
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = dayTitle,
                        onValueChange = { dayTitle = it },
                        label = { Text("Day Title (e.g., Day 4 - Back & Biceps)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberGlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_day_title_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = dayFocus,
                        onValueChange = { dayFocus = it },
                        label = { Text("Target Focus / Muscle Group") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberGlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_day_focus_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dayTitle.isNotBlank()) {
                            onAddDay(dayTitle, dayFocus)
                            showAddDayDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberVoid)
                ) {
                    Text("CREATE DAY", fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDayDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }

    // Add Exercise Sheet / Dialog (Strictly Manual Numeric Inputs, NO SLIDERS)
    if (showAddExerciseDialog) {
        var exerciseName by remember { mutableStateOf("") }
        var weightUsedInput by remember { mutableStateOf("") }
        var setsInput by remember { mutableStateOf("3") }
        var repsInput by remember { mutableStateOf("10") }
        var workSecondsInput by remember { mutableStateOf("30") }
        var restSecondsInput by remember { mutableStateOf("15") }
        var intensityTier by remember { mutableStateOf("Moderate / Hypertrophy") }
        var notes by remember { mutableStateOf("") }

        val setsVal = setsInput.toIntOrNull()?.coerceIn(1, 99) ?: 3
        val repsVal = repsInput.toIntOrNull()?.coerceIn(1, 999) ?: 10
        val workSecVal = workSecondsInput.toIntOrNull()?.coerceIn(1, 600) ?: 30
        val restSecVal = restSecondsInput.toIntOrNull()?.coerceIn(0, 600) ?: 15

        val parsedWeight = weightUsedInput.trim().toDoubleOrNull() ?: 0.0
        val weightUsedKg = if (parsedWeight > 0.0) parsedWeight else 0.0
        val isBodyweight = weightUsedKg <= 0.0
        val weightLabel = if (isBodyweight) "Bodyweight" else "${weightUsedKg} kg"

        val safetyWarning = remember(exerciseName, weightUsedKg, userProfile.weightKg, userProfile.fitnessLevel) {
            SafetyAdvisor.evaluateWeightSafety(
                exerciseName = exerciseName,
                weightKg = weightUsedKg,
                userWeightKg = userProfile.weightKg,
                fitnessLevel = userProfile.fitnessLevel
            )
        }

        // Exact calculated total duration
        val totalCalculatedSeconds = setsVal * (workSecVal + restSecVal)
        val calculatedDurationMinutes = maxOf(1, (totalCalculatedSeconds + 30) / 60)

        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            containerColor = CyberGlassSurfaceElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Smart Analyzer",
                        tint = VividLime,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚡ SMART EXERCISE ANALYZER",
                        color = VividLime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Enter movement details. Precision numeric interval timer boxes eliminate clunky sliders.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = RajdhaniFontFamily
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = exerciseName,
                        onValueChange = { exerciseName = it },
                        label = { Text("Exercise Name") },
                        placeholder = { Text("e.g., Heavy Barbell Squats, Burpees, Dips") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VividLime,
                            unfocusedBorderColor = CyberGlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exercise_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Weight Used (kg) Input Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Weight",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WEIGHT USED (KG)", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                            }
                            Text(
                                text = weightLabel,
                                color = if (isBodyweight) VividLime else NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = OrbitronFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = weightUsedInput,
                            onValueChange = { weightUsedInput = it },
                            placeholder = { Text("0 or blank = Bodyweight (e.g. 20, 60)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberGlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exercise_weight_input")
                        )
                    }

                    // Smart Safety & Overload Warning Banner
                    if (safetyWarning != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(HotAmber.copy(alpha = 0.15f))
                                .border(1.2.dp, HotAmber, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .testTag("safety_overload_warning")
                        ) {
                            Text(
                                text = safetyWarning,
                                color = HotAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sets & Reps Numeric Input Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sets manual numeric input
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("SETS COUNT", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = setsInput,
                                    onValueChange = { if (it.all { ch -> ch.isDigit() }) setsInput = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberGlassBorder,
                                        focusedTextColor = NeonCyan,
                                        unfocusedTextColor = NeonCyan
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sets_input")
                                )
                            }
                        }

                        // Reps manual numeric input
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("REPS PER SET", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = repsInput,
                                    onValueChange = { if (it.all { ch -> ch.isDigit() }) repsInput = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = VividLime,
                                        unfocusedBorderColor = CyberGlassBorder,
                                        focusedTextColor = VividLime,
                                        unfocusedTextColor = VividLime
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reps_input")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Work Interval (Seconds) Manual Numeric Box + Quick Chips
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF39FF14).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("WORK INTERVAL (SEC)", color = Color(0xFF39FF14), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                            Text("${workSecVal}s", color = Color(0xFF39FF14), fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = workSecondsInput,
                            onValueChange = { if (it.all { ch -> ch.isDigit() }) workSecondsInput = it },
                            placeholder = { Text("e.g. 20, 30, 45") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF39FF14),
                                unfocusedBorderColor = CyberGlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("work_seconds_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Quick Presets
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("10", "15", "20", "30", "40", "45", "60").forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (workSecondsInput == preset) Color(0xFF39FF14).copy(alpha = 0.2f) else Color(0xFF162032))
                                        .border(1.dp, if (workSecondsInput == preset) Color(0xFF39FF14) else CyberGlassBorder, RoundedCornerShape(4.dp))
                                        .clickable { workSecondsInput = preset }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text("${preset}s", color = if (workSecondsInput == preset) Color(0xFF39FF14) else TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rest Interval (Seconds) Manual Numeric Box + Quick Chips
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFFFF2D55).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("REST INTERVAL (SEC)", color = Color(0xFFFF2D55), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                            Text("${restSecVal}s", color = Color(0xFFFF2D55), fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = restSecondsInput,
                            onValueChange = { if (it.all { ch -> ch.isDigit() }) restSecondsInput = it },
                            placeholder = { Text("e.g. 5, 10, 15, 30") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF2D55),
                                unfocusedBorderColor = CyberGlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rest_seconds_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Quick Presets
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("0", "5", "10", "15", "20", "30", "45", "60").forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (restSecondsInput == preset) Color(0xFFFF2D55).copy(alpha = 0.2f) else Color(0xFF162032))
                                        .border(1.dp, if (restSecondsInput == preset) Color(0xFFFF2D55) else CyberGlassBorder, RoundedCornerShape(4.dp))
                                        .clickable { restSecondsInput = preset }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text("${preset}s", color = if (restSecondsInput == preset) Color(0xFFFF2D55) else TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Computed Session Timing Callout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF162238))
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        val mins = totalCalculatedSeconds / 60
                        val secs = totalCalculatedSeconds % 60
                        Text(
                            text = "TOTAL INTERVAL TIME: ${mins}m ${secs}s ($calculatedDurationMinutes min metabolic burn)",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("INTENSITY TIER", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                    Spacer(modifier = Modifier.height(4.dp))
                    val tiers = listOf(
                        "Low / Warmup",
                        "Moderate / Hypertrophy",
                        "High / Heavy Compounds",
                        "Maximum / HIIT"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        tiers.forEach { tier ->
                            val isSel = intensityTier == tier
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) VividLime.copy(alpha = 0.15f) else Color(0xFF0F172A))
                                    .border(1.dp, if (isSel) VividLime else CyberGlassBorder, RoundedCornerShape(6.dp))
                                    .clickable { intensityTier = tier }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tier,
                                    color = if (isSel) VividLime else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = RajdhaniFontFamily
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (exerciseName.isNotBlank()) {
                            onAddExercise(
                                exerciseName,
                                calculatedDurationMinutes,
                                setsVal,
                                repsVal,
                                workSecVal,
                                restSecVal,
                                weightUsedKg,
                                intensityTier,
                                notes
                            )
                            showAddExerciseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VividLime, contentColor = CyberVoid),
                    modifier = Modifier.testTag("submit_exercise_eval_button")
                ) {
                    Text("EVALUATE & LOG", fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }

    // Live Interval Workout Timer Modal
    if (showLiveTimerModal && exercises.isNotEmpty() && currentDay != null) {
        LiveIntervalTimerModal(
            dayTitle = currentDay.title,
            exercises = exercises,
            onDismiss = { showLiveTimerModal = false }
        )
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    onDelete: () -> Unit
) {
    val demandColor = when (exercise.recoveryDemand.lowercase()) {
        "low" -> VividLime
        "moderate" -> NeonCyan
        "high" -> HotAmber
        "extreme" -> NeonPink
        else -> NeonCyan
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberGlassSurface)
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("exercise_card_${exercise.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.exerciseName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${exercise.sets} sets × ${exercise.reps} reps • ${exercise.intensityTier}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = RajdhaniFontFamily
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Exercise",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Badges row: Weight Load, Work & Rest seconds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val weightDisplay = if (exercise.weightUsedKg > 0.0) {
                    if (exercise.weightUsedKg % 1.0 == 0.0) "${exercise.weightUsedKg.toInt()} kg" else "%.1f kg".format(exercise.weightUsedKg)
                } else {
                    "Bodyweight"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.12f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LOAD: $weightDisplay",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF39FF14).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFF39FF14).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "WORK: ${exercise.workSeconds}s",
                        color = Color(0xFF39FF14),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFF2D55).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFFF2D55).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "REST: ${exercise.restSeconds}s",
                        color = Color(0xFFFF2D55),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row: MET, Active Burn, EPOC Burn, Muscle Group, Recovery Demand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active Burn
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(text = "ACTIVE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                        Text(
                            text = "%.0f kcal".format(exercise.activeCaloriesBurned),
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                        Text(text = "%.1f MET".format(exercise.estimatedMet), color = TextMuted, fontSize = 8.sp, fontFamily = RajdhaniFontFamily)
                    }
                }

                // EPOC Burn
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(text = "EPOC AFTERBURN", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                        Text(
                            text = "+%.1f kcal".format(exercise.epocCaloriesBurned),
                            color = VividLime,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                        Text(text = "%.1f%% boost".format(exercise.epocPercentage), color = TextMuted, fontSize = 8.sp, fontFamily = RajdhaniFontFamily)
                    }
                }

                // Muscle & Demand
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(text = "TARGET MUSCLE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                        Text(
                            text = exercise.primaryMuscleGroup.take(16),
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = RajdhaniFontFamily
                        )
                        Text(
                            text = "Demand: ${exercise.recoveryDemand}",
                            color = demandColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                }
            }

            if (exercise.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${exercise.notes}",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = RajdhaniFontFamily
                )
            }
        }
    }
}
