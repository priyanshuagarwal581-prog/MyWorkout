package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Exercise
import com.example.ui.theme.CyberDarkNavy
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AudioCueManager
import kotlinx.coroutines.delay

enum class IntervalType { WORK, REST }

data class IntervalStep(
    val exerciseIndex: Int,
    val exerciseName: String,
    val setIndex: Int,
    val totalSets: Int,
    val type: IntervalType,
    val durationSeconds: Int,
    val nextUpLabel: String
)

private val VibrantNeonGreen = Color(0xFF39FF14)
private val VibrantNeonRed = Color(0xFFFF2D55)

@Composable
fun LiveIntervalTimerModal(
    dayTitle: String,
    exercises: List<Exercise>,
    onDismiss: () -> Unit
) {
    if (exercises.isEmpty()) {
        return
    }

    // Generate ordered interval steps
    val steps = remember(exercises) {
        val list = mutableListOf<IntervalStep>()
        exercises.forEachIndexed { exIdx, ex ->
            val setsCount = maxOf(1, ex.sets)
            val workSec = maxOf(1, ex.workSeconds)
            val restSec = maxOf(0, ex.restSeconds)

            for (set in 1..setsCount) {
                // WORK STEP
                val nextAfterWork = if (restSec > 0) {
                    "REST (${restSec}s)"
                } else if (set < setsCount) {
                    "${ex.exerciseName} (Set ${set + 1}/$setsCount)"
                } else if (exIdx < exercises.lastIndex) {
                    "${exercises[exIdx + 1].exerciseName} (Set 1/${exercises[exIdx + 1].sets})"
                } else {
                    "Final Workout Finish!"
                }

                list.add(
                    IntervalStep(
                        exerciseIndex = exIdx,
                        exerciseName = ex.exerciseName,
                        setIndex = set,
                        totalSets = setsCount,
                        type = IntervalType.WORK,
                        durationSeconds = workSec,
                        nextUpLabel = nextAfterWork
                    )
                )

                // REST STEP (if restSec > 0)
                if (restSec > 0) {
                    val nextAfterRest = if (set < setsCount) {
                        "${ex.exerciseName} (Set ${set + 1}/$setsCount)"
                    } else if (exIdx < exercises.lastIndex) {
                        "${exercises[exIdx + 1].exerciseName} (Set 1/${exercises[exIdx + 1].sets})"
                    } else {
                        "Session Completion!"
                    }

                    list.add(
                        IntervalStep(
                            exerciseIndex = exIdx,
                            exerciseName = ex.exerciseName,
                            setIndex = set,
                            totalSets = setsCount,
                            type = IntervalType.REST,
                            durationSeconds = restSec,
                            nextUpLabel = nextAfterRest
                        )
                    )
                }
            }
        }
        list
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var totalElapsedTime by remember { mutableIntStateOf(0) }

    val currentStep = steps.getOrNull(currentStepIndex)
    var stepRemainingSeconds by remember(currentStepIndex) {
        mutableIntStateOf(currentStep?.durationSeconds ?: 1)
    }

    // Play transition sound when step changes
    LaunchedEffect(currentStepIndex) {
        val step = steps.getOrNull(currentStepIndex)
        if (step != null && !isFinished) {
            stepRemainingSeconds = step.durationSeconds
            if (step.type == IntervalType.WORK) {
                AudioCueManager.playWorkTransition()
            } else {
                AudioCueManager.playRestTransition()
            }
        }
    }

    // Countdown Timer Loop
    LaunchedEffect(isPaused, isFinished, currentStepIndex) {
        while (!isPaused && !isFinished && currentStepIndex < steps.size) {
            delay(1000L)
            totalElapsedTime++

            if (stepRemainingSeconds > 1) {
                stepRemainingSeconds--
                if (stepRemainingSeconds in 1..3) {
                    AudioCueManager.playCountdownBeep()
                }
            } else {
                // Step finished! Auto-advance
                if (currentStepIndex < steps.lastIndex) {
                    currentStepIndex++
                } else {
                    isFinished = true
                    AudioCueManager.playWorkoutComplete()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberVoid)
                .testTag("interval_timer_modal")
        ) {
            if (isFinished) {
                // Workout Completed Celebration Screen
                CelebrationScreen(
                    dayTitle = dayTitle,
                    exercises = exercises,
                    totalSeconds = totalElapsedTime,
                    onClose = onDismiss
                )
            } else if (currentStep != null) {
                // Active Workout Screen
                ActiveIntervalScreen(
                    dayTitle = dayTitle,
                    currentStep = currentStep,
                    stepIndex = currentStepIndex,
                    totalSteps = steps.size,
                    remainingSeconds = stepRemainingSeconds,
                    isPaused = isPaused,
                    onTogglePause = { isPaused = !isPaused },
                    onSkipNext = {
                        if (currentStepIndex < steps.lastIndex) {
                            currentStepIndex++
                        } else {
                            isFinished = true
                            AudioCueManager.playWorkoutComplete()
                        }
                    },
                    onRestartStep = {
                        stepRemainingSeconds = currentStep.durationSeconds
                    },
                    onClose = onDismiss
                )
            }
        }
    }
}

@Composable
private fun ActiveIntervalScreen(
    dayTitle: String,
    currentStep: IntervalStep,
    stepIndex: Int,
    totalSteps: Int,
    remainingSeconds: Int,
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    onSkipNext: () -> Unit,
    onRestartStep: () -> Unit,
    onClose: () -> Unit
) {
    val isWork = currentStep.type == IntervalType.WORK
    val themeColor = if (isWork) VibrantNeonGreen else VibrantNeonRed
    val totalStepDuration = maxOf(1, currentStep.durationSeconds)
    val progressFraction = (remainingSeconds.toFloat() / totalStepDuration).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dayTitle.uppercase(),
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OrbitronFontFamily
                )
                Text(
                    text = "INTERVAL STEP ${stepIndex + 1} OF $totalSteps",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = RajdhaniFontFamily
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF162032))
                    .testTag("exit_timer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit session",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Center Clock & Progress Dial
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // State Badge: WORK / GO! or REST / BREATHE
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(themeColor.copy(alpha = 0.16f))
                    .border(1.5.dp, themeColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .testTag("interval_state_badge")
            ) {
                Text(
                    text = if (isWork) "WORK / GO!" else "REST / BREATHE",
                    color = themeColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Exercise Name & Set Info
            Text(
                text = currentStep.exerciseName,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = OrbitronFontFamily,
                maxLines = 1,
                modifier = Modifier.testTag("active_exercise_name")
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "SET ${currentStep.setIndex} OF ${currentStep.totalSets}",
                color = themeColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = RajdhaniFontFamily,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Circular Dial with Responsive Size
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .testTag("interval_countdown_clock")
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val stroke = 14.dp.toPx()
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    val topLeft = Offset(stroke / 2, stroke / 2)

                    // Background Track
                    drawArc(
                        color = Color(0xFF131D2E),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke)
                    )

                    // Glowing Progress Arc
                    drawArc(
                        color = themeColor.copy(alpha = 0.35f),
                        startAngle = -90f,
                        sweepAngle = progressFraction * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke + 6.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = themeColor,
                        startAngle = -90f,
                        sweepAngle = progressFraction * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                // Digital Clock
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = remainingSeconds / 60
                    val secs = remainingSeconds % 60
                    val timeStr = if (minutes > 0) "%02d:%02d".format(minutes, secs) else "%02d".format(secs)

                    Text(
                        text = timeStr,
                        color = themeColor,
                        fontSize = if (minutes > 0) 40.sp else 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = OrbitronFontFamily
                    )
                    Text(
                        text = "SECONDS",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Next Up Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberGlassSurfaceElevated)
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEXT UP:",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                    Text(
                        text = currentStep.nextUpLabel,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RajdhaniFontFamily
                    )
                }
            }
        }

        // Bottom Controls - elevated, fully visible above navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Replay / Restart Current Step
            IconButton(
                onClick = onRestartStep,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF162032))
                    .border(1.dp, CyberGlassBorder, CircleShape)
                    .testTag("restart_step_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Restart Step",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Big Play/Pause Button
            Button(
                onClick = onTogglePause,
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = CyberVoid
                ),
                shape = CircleShape,
                modifier = Modifier
                    .size(68.dp)
                    .testTag("toggle_pause_timer_button")
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = CyberVoid,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Skip to Next Set/Exercise
            IconButton(
                onClick = onSkipNext,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF162032))
                    .border(1.dp, CyberGlassBorder, CircleShape)
                    .testTag("skip_next_timer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip Step",
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun CelebrationScreen(
    dayTitle: String,
    exercises: List<Exercise>,
    totalSeconds: Int,
    onClose: () -> Unit
) {
    val totalActiveBurn = exercises.sumOf { it.activeCaloriesBurned }
    val totalEpocBurn = exercises.sumOf { it.epocCaloriesBurned }
    val combinedBurn = totalActiveBurn + totalEpocBurn
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Trophy Icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(VibrantNeonGreen.copy(alpha = 0.15f))
                .border(2.dp, VibrantNeonGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy",
                tint = VibrantNeonGreen,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "WORKOUT COMPLETED!",
            color = VibrantNeonGreen,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = dayTitle.uppercase(),
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = RajdhaniFontFamily
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Burn Stats Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberGlassSurfaceElevated)
                .border(1.dp, VibrantNeonGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TOTAL METABOLIC OUTPUT",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OrbitronFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "%.0f KCAL".format(combinedBurn),
                    color = VibrantNeonGreen,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily
                )
                Text(
                    text = "Active: %.0f kcal  •  EPOC Afterburn: +%.1f kcal".format(totalActiveBurn, totalEpocBurn),
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontFamily = RajdhaniFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TOTAL TIME", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "%02d:%02d".format(minutes, seconds),
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "EXERCISES", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${exercises.size}",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "SETS CRUSHED", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${exercises.sumOf { it.sets }}",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Return button
        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(
                containerColor = VibrantNeonGreen,
                contentColor = CyberVoid
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("celebration_return_button")
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Done",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RETURN TO ROUTINE DASHBOARD",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = OrbitronFontFamily
            )
        }
    }
}
