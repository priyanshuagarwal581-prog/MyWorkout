package com.example.ui.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserProfile
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.HotAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime
import kotlin.math.roundToInt

@Composable
fun OnboardingModal(
    initialProfile: UserProfile,
    onComplete: (age: Int, weightKg: Double, heightCm: Double, gender: String, fitnessLevel: String) -> Unit
) {
    var heightCm by remember { mutableDoubleStateOf(initialProfile.heightCm) }
    var weightKg by remember { mutableDoubleStateOf(initialProfile.weightKg) }
    var age by remember { mutableIntStateOf(initialProfile.age) }
    var gender by remember { mutableStateOf(initialProfile.gender) }
    var selectedFitnessLevel by remember { mutableStateOf(initialProfile.fitnessLevel) }

    val fitnessLevels = listOf(
        FitnessLevelInfo(
            title = "Beginner",
            definition = "0 to 6 months of training or starting fresh. Baseline strength, learning movement mechanics.",
            color = NeonCyan
        ),
        FitnessLevelInfo(
            title = "Intermediate",
            definition = "6 months to 2 years of consistent training. Solid form, progressing with progressive overload.",
            color = VividLime
        ),
        FitnessLevelInfo(
            title = "Advanced",
            definition = "2+ years of dedicated, structured training. High neuromuscular efficiency, mastering periodization and heavy compound lifts.",
            color = HotAmber
        )
    )

    Dialog(
        onDismissRequest = { /* Modal is mandatory on first launch until completed */ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberVoid)
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("onboarding_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo & Badge
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.5.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Fitness Center",
                        tint = NeonCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "INITIAL CALIBRATION",
                    color = NeonCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "Setup your biometrics and training background for personalized metabolic and overload safety limits.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = RajdhaniFontFamily,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Biometrics Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberGlassSurfaceElevated)
                        .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Biometrics",
                                tint = VividLime,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1. PERSONAL METRICS",
                                color = VividLime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = OrbitronFontFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Height
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("HEIGHT", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("%.0f cm".format(heightCm), color = NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                        }
                        Slider(
                            value = heightCm.toFloat(),
                            onValueChange = { heightCm = it.toDouble() },
                            valueRange = 120f..220f,
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan, inactiveTrackColor = Color(0xFF1E293B)),
                            modifier = Modifier.testTag("onboarding_height_slider")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Weight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("BODY WEIGHT", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("%.1f kg".format(weightKg), color = VividLime, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                        }
                        Slider(
                            value = weightKg.toFloat(),
                            onValueChange = { weightKg = it.toDouble() },
                            valueRange = 40f..160f,
                            colors = SliderDefaults.colors(thumbColor = VividLime, activeTrackColor = VividLime, inactiveTrackColor = Color(0xFF1E293B)),
                            modifier = Modifier.testTag("onboarding_weight_slider")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Age and Gender
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Age Stepper
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("AGE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("$age yrs", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = OrbitronFontFamily)
                                        Row {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF1E293B))
                                                    .clickable { if (age > 14) age-- },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("-", color = TextPrimary, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF1E293B))
                                                    .clickable { if (age < 99) age++ },
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("GENDER", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("Male", "Female", "Other").forEach { g ->
                                            val isSelected = gender.equals(g, ignoreCase = true)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                                    .border(1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(4.dp))
                                                    .clickable { gender = g }
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = g,
                                                    color = if (isSelected) NeonCyan else TextMuted,
                                                    fontSize = 9.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Fitness Level Selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberGlassSurfaceElevated)
                        .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Fitness Level",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2. EXPERIENCE & FITNESS LEVEL",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = OrbitronFontFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        fitnessLevels.forEach { level ->
                            val isSelected = selectedFitnessLevel.equals(level.title, ignoreCase = true)
                            val borderColor = if (isSelected) level.color else CyberGlassBorder
                            val bgColor = if (isSelected) level.color.copy(alpha = 0.12f) else Color(0xFF0F172A)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bgColor)
                                    .border(1.2.dp, borderColor, RoundedCornerShape(10.dp))
                                    .clickable { selectedFitnessLevel = level.title }
                                    .padding(12.dp)
                                    .testTag("onboarding_level_${level.title.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) level.color else Color(0xFF1E293B))
                                            .border(1.dp, level.color, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = CyberVoid,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = level.title.uppercase(),
                                            color = if (isSelected) level.color else TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = OrbitronFontFamily
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = level.definition,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontFamily = RajdhaniFontFamily,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Informative Roadmap Tip Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(HotAmber.copy(alpha = 0.12f))
                                .border(1.dp, HotAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Roadmap Info",
                                    tint = HotAmber,
                                    modifier = Modifier.size(16.dp).padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Roadmap: 0 → Intermediate takes ~6-9 months of disciplined progressive overload; Intermediate → Advanced typically takes 18-24+ months.",
                                    color = TextPrimary,
                                    fontSize = 10.sp,
                                    fontFamily = RajdhaniFontFamily,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Complete Onboarding Button
                Button(
                    onClick = {
                        onComplete(
                            age,
                            (weightKg * 10.0).roundToInt() / 10.0,
                            (heightCm * 10.0).roundToInt() / 10.0,
                            gender,
                            selectedFitnessLevel
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VividLime,
                        contentColor = CyberVoid
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("complete_onboarding_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INITIALIZE & ENTER WORKOUT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = OrbitronFontFamily,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private data class FitnessLevelInfo(
    val title: String,
    val definition: String,
    val color: Color
)
