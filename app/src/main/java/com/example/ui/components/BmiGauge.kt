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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BmiCategory
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.HotAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BmiGauge(
    bmi: Double,
    category: BmiCategory,
    minHealthyWeight: Double,
    maxHealthyWeight: Double,
    modifier: Modifier = Modifier
) {
    // Smooth animated gauge progress [0f .. 1f]
    // Gauge maps: BMI 15 to 35 -> 180 degrees sweep (from 180 to 360)
    val targetFraction = ((bmi - 15.0) / 20.0).toFloat().coerceIn(0f, 1f)
    val animatedFraction = remember { Animatable(0f) }

    LaunchedEffect(targetFraction) {
        animatedFraction.animateTo(
            targetValue = targetFraction,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    val categoryColor = when (category) {
        BmiCategory.UNDERWEIGHT -> NeonCyan
        BmiCategory.NORMAL -> VividLime
        BmiCategory.OVERWEIGHT -> HotAmber
        BmiCategory.OBESE -> NeonPink
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CyberGlassSurface)
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("bmi_gauge_container")
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
                    text = "METABOLIC BMI MATRIX",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.2.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(categoryColor.copy(alpha = 0.15f))
                        .border(1.dp, categoryColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = category.label.uppercase(),
                        color = categoryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gauge Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp, 135.dp)
                    .testTag("bmi_gauge_canvas_box")
            ) {
                Canvas(
                    modifier = Modifier
                        .size(240.dp, 135.dp)
                ) {
                    val strokeWidth = 16.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, (size.height * 2) - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val startAngle = 180f
                    val totalSweep = 180f

                    // Underweight arc (15 to 18.5 -> 3.5 / 20 = 17.5% of 180 = 31.5 deg)
                    val underSweep = (3.5f / 20f) * totalSweep
                    // Normal arc (18.5 to 25 -> 6.5 / 20 = 32.5% of 180 = 58.5 deg)
                    val normalSweep = (6.5f / 20f) * totalSweep
                    // Overweight arc (25 to 30 -> 5.0 / 20 = 25% of 180 = 45 deg)
                    val overSweep = (5.0f / 20f) * totalSweep
                    // Obese arc (30 to 35 -> 5.0 / 20 = 25% of 180 = 45 deg)
                    val obeseSweep = (5.0f / 20f) * totalSweep

                    // Background track
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = startAngle,
                        sweepAngle = totalSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Underweight
                    drawArc(
                        color = NeonCyan.copy(alpha = 0.85f),
                        startAngle = startAngle,
                        sweepAngle = underSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )

                    // Normal
                    drawArc(
                        color = VividLime,
                        startAngle = startAngle + underSweep,
                        sweepAngle = normalSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )

                    // Overweight
                    drawArc(
                        color = HotAmber,
                        startAngle = startAngle + underSweep + normalSweep,
                        sweepAngle = overSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )

                    // Obese
                    drawArc(
                        color = NeonPink,
                        startAngle = startAngle + underSweep + normalSweep + overSweep,
                        sweepAngle = obeseSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Needle Calculation
                    val needleAngleDeg = startAngle + (animatedFraction.value * totalSweep)
                    val needleRad = Math.toRadians(needleAngleDeg.toDouble())
                    val centerX = size.width / 2
                    val centerY = size.height - 8.dp.toPx()
                    val needleLength = (size.width / 2) - strokeWidth - 6.dp.toPx()

                    val endX = centerX + (needleLength * cos(needleRad)).toFloat()
                    val endY = centerY + (needleLength * sin(needleRad)).toFloat()

                    // Glow circle center
                    drawCircle(
                        color = categoryColor,
                        radius = 8.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )

                    // Glowing needle line
                    drawLine(
                        color = categoryColor.copy(alpha = 0.4f),
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY),
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Center Value Label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    Text(
                        text = "%.1f".format(bmi),
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "BMI SCORE",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Segment Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendChip(color = NeonCyan, label = "<18.5 Under")
                LegendChip(color = VividLime, label = "18.5-24.9 Norm")
                LegendChip(color = HotAmber, label = "25-29.9 Over")
                LegendChip(color = NeonPink, label = "30+ Obese")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recommended Healthy Range
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HEALTHY WEIGHT TARGET",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "%.1f kg – %.1f kg".format(minHealthyWeight, maxHealthyWeight),
                            color = VividLime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VividLime.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "METABOLIC OPTIMUM",
                            color = VividLime,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendChip(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
