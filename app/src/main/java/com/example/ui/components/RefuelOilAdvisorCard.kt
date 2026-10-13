package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HotAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime

@Composable
fun RefuelOilAdvisorCard(
    userWeightKg: Double,
    combinedBurnKcal: Double,
    loggedProteinGrams: Double = 0.0,
    loggedCarbsGrams: Double = 0.0,
    loggedHealthyOilsGrams: Double = 0.0,
    modifier: Modifier = Modifier
) {
    var expandedEducation by remember { mutableStateOf(false) }

    // Formula calculation:
    // Protein Target: 0.3g - 0.4g per kg bodyweight
    val minProteinTarget = userWeightKg * 0.3
    val maxProteinTarget = userWeightKg * 0.4

    // Glycogen Restoration Carbs: scaled to duration/intensity (0.5g - 0.8g per kg bodyweight) + burn factor
    val burnFactor = (combinedBurnKcal / 500.0).coerceIn(1.0, 1.6)
    val minCarbsTarget = userWeightKg * 0.5 * burnFactor
    val maxCarbsTarget = userWeightKg * 0.8 * burnFactor

    // Minimum Healthy Fat & Oil Threshold: 10g - 18g baseline
    val minHealthyOilsTarget = 10.0
    val maxHealthyOilsTarget = 18.0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CyberGlassSurface)
            .border(1.dp, HotAmber.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("refuel_advisor_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Essential Oils Icon",
                        tint = HotAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "POST-WORKOUT REFUEL TARGETS",
                        color = HotAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HotAmber.copy(alpha = 0.15f))
                        .border(1.dp, HotAmber, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "BIO-OPTIMIZER",
                        color = HotAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Three Macro Columns (Protein, Carbs, Essential Oils)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Protein Target
                MacroPillar(
                    title = "PROTEIN",
                    targetRange = "%.0f-%.0f g".format(minProteinTarget, maxProteinTarget),
                    rule = "0.3–0.4g / kg",
                    logged = loggedProteinGrams,
                    targetMax = maxProteinTarget,
                    color = NeonCyan,
                    modifier = Modifier.weight(1f)
                )

                // Glycogen Carbs Target
                MacroPillar(
                    title = "CARB GLYCOGEN",
                    targetRange = "%.0f-%.0f g".format(minCarbsTarget, maxCarbsTarget),
                    rule = "0.5–0.8g / kg",
                    logged = loggedCarbsGrams,
                    targetMax = maxCarbsTarget,
                    color = VividLime,
                    modifier = Modifier.weight(1f)
                )

                // Healthy Oils Baseline (10g - 18g)
                MacroPillar(
                    title = "HEALTHY OILS",
                    targetRange = "10–18 g",
                    rule = "Essential Lipids",
                    logged = loggedHealthyOilsGrams,
                    targetMax = maxHealthyOilsTarget,
                    color = HotAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Essential Fat & Oil Baseline Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberGlassSurfaceElevated)
                    .border(1.dp, HotAmber.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(HotAmber)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "10g–18g Healthy Oil Threshold",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { expandedEducation = !expandedEducation }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (expandedEducation) "Hide Science" else "View Science",
                                color = HotAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = if (expandedEducation) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle Science Details",
                                tint = HotAmber,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "Essential for endocrine steroidogenesis (testosterone/GH) & absorption of fat-soluble vitamins (A, D, E, K).",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    AnimatedVisibility(visible = expandedEducation) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(CyberGlassBorder)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "BIOLOGICAL ROLES IN POST-EXERCISE RECOVERY",
                                color = VividLime,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Endocrine Balance: Cholesterol & unsaturated fatty acids form the structural backbone of steroid hormones (testosterone, cortisol regulation).\n" +
                                        "2. Fat-Soluble Vitamin Uptake: Vitamins A, D, E, and K require micelle formation facilitated by dietary lipids for intestinal absorption.\n" +
                                        "3. Anti-Inflammatory Eicosanoids: Omega-3 EPA/DHA and Oleocanthal suppress acute post-workout muscle catabolism.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "HEALTHY OILS VS. INFLAMMATORY FATS",
                                color = HotAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Healthy
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0D2818))
                                        .border(1.dp, VividLime.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "RECOMMENDED OILS",
                                            color = VividLime,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• Extra Virgin Olive Oil\n• Avocado Oil\n• Cold-Pressed Flaxseed\n• Walnut & Chia Oil\n• Wild Marine Omega-3",
                                            color = TextPrimary,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }

                                // Inflammatory
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF280D14))
                                        .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "AVOID / INFLAMMATORY",
                                            color = NeonPink,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• Partially hydrogenated trans fats\n• Reheated/oxidized fry oils\n• High-heat refined corn/soy\n• Chemical margarine blends",
                                            color = TextPrimary,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroPillar(
    title: String,
    targetRange: String,
    rule: String,
    logged: Double,
    targetMax: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (targetMax > 0) (logged / targetMax).toFloat().coerceIn(0f, 1f) else 0f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = targetRange,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = rule,
                color = TextSecondary,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Logged: %.1fg".format(logged),
                color = if (logged >= targetMax * 0.7) color else TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
