package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodLog
import com.example.data.model.UserProfile
import com.example.ui.components.RefuelOilAdvisorCard
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HotAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime

@Composable
fun RefuelAdvisorScreen(
    userProfile: UserProfile,
    combinedBurnKcal: Double,
    foodLogs: List<FoodLog>,
    modifier: Modifier = Modifier
) {
    val loggedProtein = foodLogs.sumOf { it.proteinGrams }
    val loggedCarbs = foodLogs.sumOf { it.carbsGrams }
    val loggedHealthyOils = foodLogs.sumOf { it.healthyUnsaturatedOilsGrams }

    // Dynamic targets
    val minProteinTarget = userProfile.weightKg * 0.3
    val maxProteinTarget = userProfile.weightKg * 0.4
    val burnFactor = (combinedBurnKcal / 500.0).coerceIn(1.0, 1.6)
    val minCarbsTarget = userProfile.weightKg * 0.5 * burnFactor
    val maxCarbsTarget = userProfile.weightKg * 0.8 * burnFactor
    val minOilsTarget = 10.0
    val maxOilsTarget = 18.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberVoid)
            .padding(horizontal = 16.dp)
            .testTag("refuel_advisor_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "POST-WORKOUT REFUEL",
                        color = HotAmber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Essential Oil Threshold & Metabolic Recovery",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HotAmber.copy(alpha = 0.15f))
                        .border(1.dp, HotAmber, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "BIO-CHEMISTRY",
                        color = HotAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Dynamic Refuel Card
            RefuelOilAdvisorCard(
                userWeightKg = userProfile.weightKg,
                combinedBurnKcal = combinedBurnKcal,
                loggedProteinGrams = loggedProtein,
                loggedCarbsGrams = loggedCarbs,
                loggedHealthyOilsGrams = loggedHealthyOils
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Macro Delivery Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberGlassSurface)
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "METABOLIC ADAPTATION TARGET STATUS",
                        color = VividLime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TargetProgressBar(
                        label = "Muscle Protein Synthesis",
                        logged = loggedProtein,
                        targetMin = minProteinTarget,
                        targetMax = maxProteinTarget,
                        color = NeonCyan,
                        unit = "g"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TargetProgressBar(
                        label = "Glycogen Supercompensation",
                        logged = loggedCarbs,
                        targetMin = minCarbsTarget,
                        targetMax = maxCarbsTarget,
                        color = VividLime,
                        unit = "g"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TargetProgressBar(
                        label = "Essential Healthy Oils & Endocrine Fats",
                        logged = loggedHealthyOils,
                        targetMin = minOilsTarget,
                        targetMax = maxOilsTarget,
                        color = HotAmber,
                        unit = "g"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // In-Depth Oil Biochemical Advisor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberGlassSurfaceElevated)
                    .border(1.dp, HotAmber.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Science",
                            tint = HotAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WHY 10g–18g OF HEALTHY OILS IS MANDATORY",
                            color = HotAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Fat-free post-workout meals starve hormonal recovery. Consuming a baseline of 10g to 18g of unheated, cold-pressed unsaturated oils activates three vital post-workout pathways:\n\n" +
                                "1. Endocrine Steroidogenesis:\n" +
                                "Testosterone and growth hormone production require cholesterol and essential fatty acid precursors. Extremely low fat intake impairs pituitary-gonadal signaling.\n\n" +
                                "2. Fat-Soluble Vitamin Uptake:\n" +
                                "Vitamins A (epithelial tissue repair), D (calcium balance & muscle contraction), E (lipid peroxidation defense), and K (bone matrix mineralization) cannot cross the intestinal wall without lipid micelles.\n\n" +
                                "3. Cellular Membrane Fluidity:\n" +
                                "Heavy concentric and eccentric contractions create micro-tears in muscle sarcolemma. Omega-3 phospholipids (EPA & DHA) integrate into cell walls, restoring elasticity and signaling speed.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post-Workout Meal Recommendation Matrix
            Text(
                text = "POST-WORKOUT BIO-OPTIMAL RECIPES",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            MealPrescriptionCard(
                name = "Wild Salmon & Cold-Pressed Olive Quinoa",
                macros = "42g Protein • 38g Carbs • 16.5g Healthy Oils",
                lipids = "Extra Virgin Olive Oil (Oleocanthal) + Marine EPA/DHA",
                vitamins = "Vitamins D, B12, E & Selenium"
            )

            Spacer(modifier = Modifier.height(10.dp))

            MealPrescriptionCard(
                name = "Pasture Eggs & Avocado Sourdough",
                macros = "26g Protein • 32g Carbs • 14g Healthy Oils",
                lipids = "Avocado Monounsaturated Lipids + Egg Yolk Choline",
                vitamins = "Vitamins A, D, B2 & Lutein"
            )

            Spacer(modifier = Modifier.height(10.dp))

            MealPrescriptionCard(
                name = "Whey Isolate + 1 Tbsp Cold-Pressed Flaxseed Oil",
                macros = "34g Protein • 24g Carbs • 14g Healthy Oils",
                lipids = "Flaxseed Alpha-Linolenic Acid (ALA Omega-3)",
                vitamins = "Fast-digesting leucine surge with lipid membrane coat"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TargetProgressBar(
    label: String,
    logged: Double,
    targetMin: Double,
    targetMax: Double,
    color: Color,
    unit: String
) {
    val progress = (logged / targetMax).toFloat().coerceIn(0f, 1f)
    val isMet = logged >= targetMin

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "%.1f / %.0f-%.0f %s".format(logged, targetMin, targetMax, unit),
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                if (isMet) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Met Target",
                        tint = VividLime,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun MealPrescriptionCard(
    name: String,
    macros: String,
    lipids: String,
    vitamins: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberGlassSurface)
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = macros, color = VividLime, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Oil Source: $lipids", color = HotAmber, fontSize = 10.sp)
            Text(text = "Micronutrients: $vitamins", color = NeonCyan, fontSize = 10.sp)
        }
    }
}
