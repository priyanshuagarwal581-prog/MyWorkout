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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.FoodLog
import com.example.ui.components.CyberSkeletonCard
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassSurface
import com.example.ui.theme.CyberGlassSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.HotAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividLime

@Composable
fun FoodInspectorScreen(
    foodLogs: List<FoodLog>,
    isInspectingFood: Boolean,
    onInspectAndAddFood: (String) -> Unit,
    onDeleteFoodLog: (FoodLog) -> Unit,
    modifier: Modifier = Modifier
) {
    var mealInput by remember { mutableStateOf("") }

    val totalCalories = foodLogs.sumOf { it.calories }
    val totalProtein = foodLogs.sumOf { it.proteinGrams }
    val totalCarbs = foodLogs.sumOf { it.carbsGrams }
    val totalFat = foodLogs.sumOf { it.totalFatGrams }
    val totalHealthyOils = foodLogs.sumOf { it.healthyUnsaturatedOilsGrams }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberVoid)
            .padding(horizontal = 16.dp)
            .testTag("food_inspector_screen")
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
                        text = "FOOD & VITAMIN INSPECTOR",
                        color = HotAmber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Biochemical Micronutrient & Healthy Oil Breakdown",
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
                        text = "⚡ LIVE BIO-SCAN",
                        color = HotAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Food Input Search / Analyzer Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(CyberGlassSurface)
                    .border(1.dp, HotAmber.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ENTER ANY MEAL, DISH, OR RECOVERY SNACK",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = mealInput,
                        onValueChange = { mealInput = it },
                        placeholder = { Text("e.g., 2 boiled eggs with brown bread and 1 tsp butter") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HotAmber,
                            unfocusedBorderColor = CyberGlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CyberGlassSurfaceElevated,
                            unfocusedContainerColor = CyberGlassSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("food_description_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Quick Suggestions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "2 boiled eggs & avocado toast",
                            "150g grilled salmon with olive oil",
                            "Greek yogurt with chia seeds"
                        ).forEach { sample ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(6.dp))
                                    .clickable { mealInput = sample }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = sample.take(18) + "...",
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (mealInput.isNotBlank()) {
                                onInspectAndAddFood(mealInput)
                                mealInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HotAmber,
                            contentColor = CyberVoid
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("inspect_food_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Scan Nutrition",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⚡ SCAN MICRONUTRIENTS & OILS",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading Skeleton
            if (isInspectingFood) {
                CyberSkeletonCard(statusMessage = "EXTRACTING VITAMINS & HEALTHY OILS...")
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Daily Macro Totals Ribbon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberGlassSurfaceElevated)
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S LOGGED NUTRITION",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "%.0f kcal".format(totalCalories),
                            color = HotAmber,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroStat(label = "PROTEIN", value = "%.0fg".format(totalProtein), color = NeonCyan)
                        MacroStat(label = "CARBS", value = "%.0fg".format(totalCarbs), color = VividLime)
                        MacroStat(label = "TOTAL FAT", value = "%.0fg".format(totalFat), color = TextPrimary)
                        MacroStat(label = "HEALTHY OILS", value = "%.1fg".format(totalHealthyOils), color = HotAmber)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "LOGGED MEALS & FOOD ANALYSES (${foodLogs.size})",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (foodLogs.isEmpty() && !isInspectingFood) {
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
                            imageVector = Icons.Default.LocalDining,
                            contentDescription = "Empty Meals",
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "NO MEALS LOGGED YET",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Enter what you ate above to uncover vitamins, minerals & recovery impact",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        items(foodLogs, key = { it.id }) { food ->
            FoodLogCard(
                foodLog = food,
                onDelete = { onDeleteFoodLog(food) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MacroStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(
            text = value,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun FoodLogCard(
    foodLog: FoodLog,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberGlassSurface)
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("food_log_card_${foodLog.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = foodLog.foodName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = foodLog.servingSize,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Food",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Macro row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroPill(label = "CALORIES", value = "%.0f".format(foodLog.calories), unit = "kcal", color = HotAmber, modifier = Modifier.weight(1f))
                MacroPill(label = "PROTEIN", value = "%.1f".format(foodLog.proteinGrams), unit = "g", color = NeonCyan, modifier = Modifier.weight(1f))
                MacroPill(label = "CARBS", value = "%.1f".format(foodLog.carbsGrams), unit = "g", color = VividLime, modifier = Modifier.weight(1f))
                MacroPill(label = "HEALTHY OILS", value = "%.1f".format(foodLog.healthyUnsaturatedOilsGrams), unit = "g", color = HotAmber, modifier = Modifier.weight(1f))
            }

            // Vitamins and Minerals Pills
            if (foodLog.mineralsList.isNotEmpty() || foodLog.vitaminsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    foodLog.vitaminsList.take(3).forEach { vit ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = vit, color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    foodLog.mineralsList.take(3).forEach { min ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VividLime.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = min, color = VividLime, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Recovery impact note
            if (foodLog.recoveryImpact.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Recovery Impact: ${foodLog.recoveryImpact}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroPill(label: String, value: String, unit: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .padding(6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "$value $unit",
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
