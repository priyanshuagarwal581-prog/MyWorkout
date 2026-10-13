package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime

@Composable
fun DualRingBurnDial(
    activeCalories: Double,
    epocCalories: Double,
    activeTargetKcal: Double = 600.0,
    epocTargetKcal: Double = 90.0,
    modifier: Modifier = Modifier
) {
    val combinedCalories = activeCalories + epocCalories

    // Progress fractions normalized against target (or 1.0)
    val activeFractionTarget = (activeCalories / activeTargetKcal).toFloat().coerceIn(0f, 1f)
    val epocFractionTarget = (epocCalories / epocTargetKcal).toFloat().coerceIn(0f, 1f)

    val animatedActive = remember { Animatable(0f) }
    val animatedEpoc = remember { Animatable(0f) }

    LaunchedEffect(activeFractionTarget, epocFractionTarget) {
        animatedActive.animateTo(
            targetValue = activeFractionTarget,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        animatedEpoc.animateTo(
            targetValue = epocFractionTarget,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CyberGlassSurface)
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("dual_ring_burn_dial_container")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "METABOLIC TOTALS",
                    color = VividLime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.2.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.12f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "EPOC DUAL-RING",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Radial Rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val outerStroke = 12.dp.toPx()
                    val innerStroke = 10.dp.toPx()
                    val centerOffset = Offset(size.width / 2, size.height / 2)

                    // Outer Ring: Active Burn (Neon Cyan)
                    val outerRadius = (size.width - outerStroke) / 2
                    val outerTopLeft = Offset(outerStroke / 2, outerStroke / 2)
                    val outerSize = Size(size.width - outerStroke, size.height - outerStroke)

                    // Track
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = outerTopLeft,
                        size = outerSize,
                        style = Stroke(width = outerStroke)
                    )
                    // Progress
                    drawArc(
                        color = NeonCyan,
                        startAngle = -90f,
                        sweepAngle = animatedActive.value * 360f,
                        useCenter = false,
                        topLeft = outerTopLeft,
                        size = outerSize,
                        style = Stroke(width = outerStroke, cap = StrokeCap.Round)
                    )

                    // Inner Ring: EPOC Afterburn (Vivid Lime)
                    val gap = 16.dp.toPx()
                    val innerTopLeft = Offset((outerStroke / 2) + gap, (outerStroke / 2) + gap)
                    val innerSize = Size(
                        size.width - outerStroke - (gap * 2),
                        size.height - outerStroke - (gap * 2)
                    )

                    // Track
                    drawArc(
                        color = Color(0xFF132030),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = innerTopLeft,
                        size = innerSize,
                        style = Stroke(width = innerStroke)
                    )
                    // Progress
                    drawArc(
                        color = VividLime,
                        startAngle = -90f,
                        sweepAngle = animatedEpoc.value * 360f,
                        useCenter = false,
                        topLeft = innerTopLeft,
                        size = innerSize,
                        style = Stroke(width = innerStroke, cap = StrokeCap.Round)
                    )
                }

                // Center Combined Stats
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "%.0f".format(combinedCalories),
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "TOTAL KCAL",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "COMBINED BURN",
                        color = NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Dual ring breakdown cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Active Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ACTIVE BURN",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.1f kcal".format(activeCalories),
                            color = NeonCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Target: %.0f kcal".format(activeTargetKcal),
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }

                // EPOC Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, VividLime.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(VividLime)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EPOC AFTERBURN",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.1f kcal".format(epocCalories),
                            color = VividLime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        val ratio = if (activeCalories > 0) (epocCalories / activeCalories) * 100 else 0.0
                        Text(
                            text = "Afterburn: %.1f%%".format(ratio),
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
