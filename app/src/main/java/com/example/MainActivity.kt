package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WorkoutViewModel
import com.example.ui.components.OnboardingModal
import com.example.ui.screens.FoodInspectorScreen
import com.example.ui.screens.ProfileBmiScreen
import com.example.ui.screens.RefuelAdvisorScreen
import com.example.ui.screens.WorkoutPlannerScreen
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.HotAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividLime

enum class NavTab(val label: String, val icon: ImageVector, val tag: String) {
    PLANNER("Planner", Icons.Default.FitnessCenter, "nav_tab_planner"),
    FOOD("Food & Vitamins", Icons.Default.Restaurant, "nav_tab_food"),
    REFUEL("Refuel & Oils", Icons.Default.WaterDrop, "nav_tab_refuel"),
    PROFILE("Profile & BMI", Icons.Default.Person, "nav_tab_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyWorkoutApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWorkoutApp(viewModel: WorkoutViewModel = viewModel()) {
    var currentTab by rememberSaveable { mutableStateOf(NavTab.PLANNER) }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val workoutDays by viewModel.workoutDays.collectAsStateWithLifecycle()
    val selectedDayId by viewModel.selectedDayId.collectAsStateWithLifecycle()
    val currentExercises by viewModel.currentExercises.collectAsStateWithLifecycle()
    val foodLogs by viewModel.foodLogs.collectAsStateWithLifecycle()
    val isEvaluatingExercise by viewModel.isEvaluatingExercise.collectAsStateWithLifecycle()
    val isInspectingFood by viewModel.isInspectingFood.collectAsStateWithLifecycle()
    val bannerNotice by viewModel.bannerNotice.collectAsStateWithLifecycle()

    val combinedBurnTotal = currentExercises.sumOf { it.activeCaloriesBurned + it.epocCaloriesBurned }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberVoid,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(end = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MY WORKOUT",
                                color = NeonCyan,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.2.sp
                            )
                        }

                        // Status Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VividLime.copy(alpha = 0.15f))
                                .border(1.dp, VividLime.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Smart Metabolic",
                                    tint = VividLime,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "⚡ SMART METABOLIC",
                                    color = VividLime,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberVoid,
                    titleContentColor = NeonCyan
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberGlassSurfaceElevated,
                contentColor = NeonCyan,
                modifier = Modifier
                    .border(
                        androidx.compose.foundation.BorderStroke(1.dp, CyberGlassBorder),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .testTag("bottom_nav_bar")
            ) {
                NavTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    val tabColor = when (tab) {
                        NavTab.PLANNER -> NeonCyan
                        NavTab.FOOD -> HotAmber
                        NavTab.REFUEL -> VividLime
                        NavTab.PROFILE -> NeonCyan
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = tabColor,
                            selectedTextColor = tabColor,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = tabColor.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.PLANNER -> {
                    WorkoutPlannerScreen(
                        userProfile = userProfile,
                        workoutDays = workoutDays,
                        selectedDayId = selectedDayId,
                        exercises = currentExercises,
                        isEvaluatingExercise = isEvaluatingExercise,
                        onSelectDay = { viewModel.selectDay(it) },
                        onAddDay = { title, focus -> viewModel.addWorkoutDay(title, focus) },
                        onDeleteDay = { viewModel.deleteWorkoutDay(it) },
                        onAddExercise = { name, duration, sets, reps, workSec, restSec, weightUsed, intensity, notes ->
                            viewModel.evaluateAndAddExercise(
                                exerciseName = name,
                                durationMinutes = duration,
                                sets = sets,
                                reps = reps,
                                workSeconds = workSec,
                                restSeconds = restSec,
                                weightUsedKg = weightUsed,
                                intensityTier = intensity,
                                notes = notes
                            )
                        },
                        onDeleteExercise = { viewModel.deleteExercise(it) }
                    )
                }

                NavTab.FOOD -> {
                    FoodInspectorScreen(
                        foodLogs = foodLogs,
                        isInspectingFood = isInspectingFood,
                        onInspectAndAddFood = { viewModel.inspectAndAddFood(it) },
                        onDeleteFoodLog = { viewModel.deleteFoodLog(it) }
                    )
                }

                NavTab.REFUEL -> {
                    RefuelAdvisorScreen(
                        userProfile = userProfile,
                        combinedBurnKcal = combinedBurnTotal,
                        foodLogs = foodLogs
                    )
                }

                NavTab.PROFILE -> {
                    ProfileBmiScreen(
                        userProfile = userProfile,
                        onSaveProfile = { height, weight, age, gender, fitnessLevel ->
                            viewModel.updateProfile(height, weight, age, gender, fitnessLevel)
                        },
                        onSubmitFeedback = { viewModel.submitFeedback(it) }
                    )
                }
            }

            // One-Time Onboarding Modal on First Launch
            if (!userProfile.onboardingCompleted) {
                OnboardingModal(
                    initialProfile = userProfile,
                    onComplete = { age, weightKg, heightCm, gender, fitnessLevel ->
                        viewModel.completeOnboarding(age, weightKg, heightCm, gender, fitnessLevel)
                    }
                )
            }

            // Banner Notification Toast
            AnimatedVisibility(
                visible = bannerNotice != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                bannerNotice?.let { notice ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberGlassSurfaceElevated)
                            .border(1.dp, NeonCyan.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("banner_notification")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notice,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearBanner() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss notice",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
