package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.BmiGauge
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.HotAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime
import kotlin.math.roundToInt

@Composable
fun ProfileBmiScreen(
    userProfile: UserProfile,
    onSaveProfile: (heightCm: Double, weightKg: Double, age: Int, gender: String, fitnessLevel: String) -> Unit,
    onSubmitFeedback: (text: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var heightCm by remember(userProfile.heightCm) { mutableDoubleStateOf(userProfile.heightCm) }
    var weightKg by remember(userProfile.weightKg) { mutableDoubleStateOf(userProfile.weightKg) }
    var age by remember(userProfile.age) { mutableIntStateOf(userProfile.age) }
    var gender by remember(userProfile.gender) { mutableStateOf(userProfile.gender) }
    var fitnessLevel by remember(userProfile.fitnessLevel) { mutableStateOf(userProfile.fitnessLevel) }
    var feedbackText by remember { mutableStateOf("") }

    // Instant dynamic preview profile
    val liveProfile = remember(heightCm, weightKg, age, gender) {
        UserProfile(
            id = 1,
            heightCm = (heightCm * 10.0).roundToInt() / 10.0,
            weightKg = (weightKg * 10.0).roundToInt() / 10.0,
            age = age,
            gender = gender
        )
    }

    // Basal Metabolic Rate (Mifflin-St Jeor formula)
    val bmr = remember(heightCm, weightKg, age, gender) {
        val s = if (gender == "Male") 5 else -161
        (10 * weightKg) + (6.25 * heightCm) - (5 * age) + s
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberVoid)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_bmi_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BIOMETRIC PROFILER",
                    color = NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Dynamic BMI & Metabolic Calibration",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonCyan.copy(alpha = 0.12f))
                    .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LIVE ENGINE",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMI Interactive Radial Gauge
        BmiGauge(
            bmi = liveProfile.bmi,
            category = liveProfile.bmiCategory,
            minHealthyWeight = liveProfile.minHealthyWeightKg,
            maxHealthyWeight = liveProfile.maxHealthyWeightKg
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Controls Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CyberGlassSurface)
                .border(1.dp, CyberGlassBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PHYSIOLOGICAL INPUTS",
                    color = VividLime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Height Slider & Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Height,
                            contentDescription = "Height Icon",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HEIGHT (CM)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "%.0f cm".format(heightCm),
                        color = NeonCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Slider(
                    value = heightCm.toFloat(),
                    onValueChange = { heightCm = it.toDouble() },
                    valueRange = 120f..220f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("height_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Weight Slider & Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonitorWeight,
                            contentDescription = "Weight Icon",
                            tint = VividLime,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BODY WEIGHT (KG)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "%.1f kg".format(weightKg),
                        color = VividLime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Slider(
                    value = weightKg.toFloat(),
                    onValueChange = { weightKg = it.toDouble() },
                    valueRange = 40f..160f,
                    colors = SliderDefaults.colors(
                        thumbColor = VividLime,
                        activeTrackColor = VividLime,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("weight_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Age & Gender Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Age
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberGlassSurfaceElevated)
                            .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "AGE",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$age yrs",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1E293B))
                                            .clickable { if (age > 14) age-- }
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("-", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1E293B))
                                            .clickable { if (age < 99) age++ }
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Gender Chips
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberGlassSurfaceElevated)
                            .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "GENDER",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Male", "Female", "Other").forEach { g ->
                                    val isSelected = gender.equals(g, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E293B)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonCyan else Color.Transparent,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { gender = g }
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = g,
                                            color = if (isSelected) NeonCyan else TextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fitness Level Selector
                Text(
                    text = "TRAINING EXPERIENCE & FITNESS LEVEL",
                    color = VividLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val levelOptions = listOf(
                    Triple("Beginner", "0 to 6 months of training or starting fresh. Baseline strength, learning movement mechanics.", NeonCyan),
                    Triple("Intermediate", "6 months to 2 years of consistent training. Solid form, progressing with progressive overload.", VividLime),
                    Triple("Advanced", "2+ years of dedicated, structured training. High neuromuscular efficiency, mastering periodization and heavy compound lifts.", HotAmber)
                )

                levelOptions.forEach { (title, desc, color) ->
                    val isSel = fitnessLevel.equals(title, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) color.copy(alpha = 0.14f) else Color(0xFF0F172A))
                            .border(1.2.dp, if (isSel) color else CyberGlassBorder, RoundedCornerShape(8.dp))
                            .clickable { fitnessLevel = title }
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title.uppercase(),
                                    color = if (isSel) color else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (isSel) {
                                    Text("ACTIVE", color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Roadmap tip banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(HotAmber.copy(alpha = 0.12f))
                        .border(1.dp, HotAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Roadmap",
                            tint = HotAmber,
                            modifier = Modifier.size(14.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Roadmap: 0 → Intermediate takes ~6-9 months of disciplined progressive overload; Intermediate → Advanced typically takes 18-24+ months.",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Profile Button
                Button(
                    onClick = {
                        onSaveProfile(
                            (heightCm * 10.0).roundToInt() / 10.0,
                            (weightKg * 10.0).roundToInt() / 10.0,
                            age,
                            gender,
                            fitnessLevel
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = CyberVoid
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Save Icon",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOCK IN BIOMETRIC PROFILE",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMR & Energy Intelligence Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberGlassSurface)
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "BASE METABOLIC (BMR)",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.0f kcal/day".format(bmr),
                        color = HotAmber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Mifflin-St Jeor resting baseline",
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberGlassSurface)
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "ESTIMATED TDEE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.0f kcal/day".format(bmr * 1.55),
                        color = VividLime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Moderate training active output",
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Community & Feedback Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CyberGlassSurface)
                .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "COMMUNITY FEEDBACK & FEATURE REQUESTS",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Help us refine metabolic models, essential oil suggestions, or interval timer features.",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholder = { Text("Share suggestions, exercise requests, or bug reports...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("feedback_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (feedbackText.isNotBlank()) {
                            onSubmitFeedback(feedbackText)
                            feedbackText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberGlassSurfaceElevated,
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier.align(Alignment.End).testTag("submit_feedback_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SUBMIT FEEDBACK", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
