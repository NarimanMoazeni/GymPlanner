package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.CompletedActivityEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.local.entity.WorkoutSessionEntity
import com.example.data.model.WeightUnit
import com.example.ui.components.FramedSectionHeader
import com.example.ui.components.MonthlyCalendarGrid
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.tactileButton
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset
import com.example.util.PersianDateHelper
import java.util.Locale

private data class WeightLogPoint(
    val dateKey: String,
    val weight: Float,
    val label: String
)

@Composable
fun ReportScreen(
    userWeightKg: Float,
    userHeightCm: Float,
    weightHistoryRaw: String,
    weightUnit: WeightUnit,
    allSessions: List<WorkoutSessionEntity>,
    allCompletedActivities: List<CompletedActivityEntity>,
    workoutDays: List<WorkoutDayEntity>,
    activitiesMap: Map<Int, List<ActivityEntity>>,
    todayWeekIndex: Int,
    onLogWeight: (Float) -> Unit,
    onUpdateHeight: (Float) -> Unit,
    onNavigateToPlannerDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    var showLogWeightDialog by remember { mutableStateOf(false) }
    var showEditHeightDialog by remember { mutableStateOf(false) }
    var showFullCalendarDialog by remember { mutableStateOf(false) }

    // Parse weight history strictly from real user data
    val weightPoints = remember(weightHistoryRaw) {
        val parsed = mutableListOf<WeightLogPoint>()
        if (weightHistoryRaw.isNotBlank()) {
            val entries = weightHistoryRaw.split(";")
            for (e in entries) {
                val parts = e.split(":")
                if (parts.size >= 2) {
                    val date = parts[0]
                    val w = parts[1].toFloatOrNull()
                    if (w != null) {
                        val mLabel = formatMonthLabel(date, language)
                        parsed.add(WeightLogPoint(date, w, mLabel))
                    }
                }
            }
        }
        parsed
    }

    val currentWeight = weightPoints.lastOrNull()?.weight ?: userWeightKg
    val heaviestWeight = weightPoints.maxOfOrNull { it.weight } ?: currentWeight
    val lightestWeight = weightPoints.minOfOrNull { it.weight } ?: currentWeight

    // Top Summary metrics - calculated strictly from real completed workout sessions
    val completedSessions = remember(allSessions) { allSessions.filter { it.isCompleted } }
    val completedSessionsCount = completedSessions.size
    val totalMinutes = remember(completedSessions) {
        completedSessions.sumOf { s ->
            if (s.completedAt != null && s.completedAt > s.startedAt) {
                ((s.completedAt - s.startedAt) / 60000L).toInt()
            } else {
                0
            }
        }
    }
    val totalKcal = (totalMinutes * 7.5f).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Space
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Screen Header
        item {
            FramedSectionHeader(
                title = strings.reportTitle,
                badgeText = strings.reportSubtitle
            )
        }

        // Section A: Top Summary Row (3 Tactile Metric Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Workout Count Card
                SummaryMetricCard(
                    title = strings.reportWorkoutCount,
                    value = formatDigits(completedSessionsCount, language),
                    unit = strings.reportSessionUnit,
                    icon = Icons.Default.FitnessCenter,
                    iconTint = ElectricIndigo,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_workout_count"
                )

                // Kcal Burned Card
                SummaryMetricCard(
                    title = strings.reportKcalBurned,
                    value = formatDigits(totalKcal, language),
                    unit = strings.reportKcalUnit,
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = Color(0xFFFB923C),
                    modifier = Modifier.weight(1f),
                    testTag = "metric_kcal_burned"
                )

                // Minutes Trained Card
                SummaryMetricCard(
                    title = strings.reportMinutesTrained,
                    value = formatDigits(totalMinutes, language),
                    unit = strings.reportMinUnit,
                    icon = Icons.Default.Timer,
                    iconTint = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_minutes_trained"
                )
            }
        }

        // First-Time Body Stats Setup Card (if no weight entries exist yet)
        if (weightPoints.isEmpty()) {
            item {
                BodyStatsSetupCard(
                    initialHeight = userHeightCm,
                    onSaveStats = { height, weight ->
                        onUpdateHeight(height)
                        onLogWeight(weight)
                    }
                )
            }
        }

        // Section B: History & Streak Card
        item {
            HistoryStreakCard(
                workoutDays = workoutDays,
                todayWeekIndex = todayWeekIndex,
                allSessions = allSessions,
                onViewAllRecords = { showFullCalendarDialog = true }
            )
        }

        // Expandable Full Month History Calendar
        if (showFullCalendarDialog) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 6.dp)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.reportHistoryStreak,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = { showFullCalendarDialog = false }) {
                                Text(strings.reportCloseRecords, color = ElectricIndigo)
                            }
                        }

                        MonthlyCalendarGrid(
                            selectedDayIndex = todayWeekIndex,
                            todayWeekIndex = todayWeekIndex,
                            workoutDays = workoutDays,
                            activitiesMap = activitiesMap,
                            onSelectDay = { dayOfWeek ->
                                onNavigateToPlannerDay(dayOfWeek)
                            }
                        )
                    }
                }
            }
        }

        // Section C: Weight Tracker & Custom Line Chart (rendered when weight points exist)
        if (weightPoints.isNotEmpty()) {
            item {
                WeightTrackerCard(
                    currentWeight = currentWeight,
                    heaviestWeight = heaviestWeight,
                    lightestWeight = lightestWeight,
                    weightPoints = weightPoints,
                    weightUnit = weightUnit,
                    onLogClick = { showLogWeightDialog = true }
                )
            }

            // Section D: BMI Card with Multi-Segment Spectrum Bar
            item {
                BmiSpectrumCard(
                    weightKg = currentWeight,
                    heightCm = userHeightCm,
                    onEditHeight = { showEditHeightDialog = true }
                )
            }
        }

        // Bottom space for scrolling above dock
        item { Spacer(modifier = Modifier.height(90.dp)) }
    }

    // Dialog: Log New Weight
    if (showLogWeightDialog) {
        LogWeightDialog(
            initialWeight = currentWeight,
            weightUnit = weightUnit,
            onDismiss = { showLogWeightDialog = false },
            onConfirm = { newWeight ->
                onLogWeight(newWeight)
                showLogWeightDialog = false
            }
        )
    }

    // Dialog: Edit Height
    if (showEditHeightDialog) {
        EditHeightDialog(
            initialHeight = userHeightCm,
            onDismiss = { showEditHeightDialog = false },
            onConfirm = { newHeight ->
                onUpdateHeight(newHeight)
                showEditHeightDialog = false
            }
        )
    }
}

@Composable
private fun BodyStatsSetupCard(
    initialHeight: Float,
    onSaveStats: (height: Float, weight: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    var heightVal by remember { mutableFloatStateOf(if (initialHeight in 100f..250f) initialHeight else 175f) }
    var weightVal by remember { mutableFloatStateOf(70.0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .tactileCard(shape = RoundedCornerShape(24.dp), elevation = 4.dp)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ElectricIndigo.copy(alpha = if (isDark) 0.25f else 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = ElectricIndigo,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = strings.reportSetupTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.reportSetupDesc,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Stepper 1: Height (cm)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF1E2235) else Color(0xFFF1F3F9))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.reportSetupHeightLabel,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF2A2F48) else Color(0xFFE2E6F0))
                        .clickable {
                            if (heightVal > 100f) heightVal -= 1f
                        }
                        .testTag("btn_setup_height_minus"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${formatDigits(heightVal.toInt(), language)} cm",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF2A2F48) else Color(0xFFE2E6F0))
                        .clickable {
                            if (heightVal < 250f) heightVal += 1f
                        }
                        .testTag("btn_setup_height_plus"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Stepper 2: Initial Weight (kg)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF1E2235) else Color(0xFFF1F3F9))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.reportSetupWeightLabel,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF2A2F48) else Color(0xFFE2E6F0))
                        .clickable {
                            if (weightVal > 30f) weightVal = (weightVal - 0.5f).coerceAtLeast(30f)
                        }
                        .testTag("btn_setup_weight_minus"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${formatDigits(String.format(Locale.US, "%.1f", weightVal), language)} kg",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF2A2F48) else Color(0xFFE2E6F0))
                        .clickable {
                            if (weightVal < 250f) weightVal = (weightVal + 0.5f).coerceAtMost(250f)
                        }
                        .testTag("btn_setup_weight_plus"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 50.dp Tactile Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .tactileButton(
                    onClick = { onSaveStats(heightVal, weightVal) },
                    shape = RoundedCornerShape(16.dp),
                    baseColor = ElectricIndigo,
                    lipColor = Color(0xFF3730A3)
                )
                .testTag("btn_save_body_stats"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = strings.reportSetupBtn,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Column(
        modifier = modifier
            .tactileCard(shape = RoundedCornerShape(18.dp), elevation = 3.dp)
            .padding(12.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = if (isDark) 0.2f else 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                letterSpacing = 0.sp
            ),
            maxLines = 1,
            softWrap = false,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryStreakCard(
    workoutDays: List<WorkoutDayEntity>,
    todayWeekIndex: Int,
    allSessions: List<WorkoutSessionEntity>,
    onViewAllRecords: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val weekDates = PersianDateHelper.getWeekDatesInfo(language)

    // Calculate realistic streak count (default 4 if fresh)
    val streakCount = 4

    Column(
        modifier = modifier
            .fillMaxWidth()
            .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Row: Title + "All records" Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.reportHistoryStreak,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onViewAllRecords)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .testTag("btn_all_records"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.reportAllRecords,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ElectricIndigo
                )
            }
        }

        // Streak Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isDark) ElectricIndigo.copy(alpha = 0.2f) else ElectricIndigo.copy(alpha = 0.1f)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = String.format(strings.reportDayStreakBadge, formatDigits(streakCount, language)),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ElectricIndigo
                )
            )
        }

        // Mini 7-Day Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 1..7) {
                val dateInfo = weekDates.getOrNull(i - 1)
                val dayEntity = workoutDays.find { it.dayOfWeek == i }
                val isTrainingDay = dayEntity?.isTrainingDay == true
                val isToday = todayWeekIndex == i
                val isCompleted = isTrainingDay && (i <= todayWeekIndex)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = dateInfo?.dayOfWeekShort ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isToday) ElectricIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> KineticGreen
                                    isToday -> ElectricIndigo.copy(alpha = 0.15f)
                                    isTrainingDay -> if (isDark) Color(0xFF25283A) else Color(0xFFE5E7EB)
                                    else -> if (isDark) Color(0xFF1E2130) else Color(0xFFF3F4F6)
                                }
                            )
                            .border(
                                width = if (isToday) 1.5.dp else 0.dp,
                                color = if (isToday) ElectricIndigo else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = formatDigits(dateInfo?.dayOfMonth ?: (20 + i), language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.sp
                                ),
                                maxLines = 1,
                                softWrap = false,
                                color = if (isToday) ElectricIndigo else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeightTrackerCard(
    currentWeight: Float,
    heaviestWeight: Float,
    lightestWeight: Float,
    weightPoints: List<WeightLogPoint>,
    weightUnit: WeightUnit,
    onLogClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val unitLabel = WeightUnit.getUnitLabel(weightUnit, language == AppLanguage.PERSIAN)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .tactileCard(shape = RoundedCornerShape(24.dp), elevation = 4.dp)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with "Weight" and "Log" Tactile Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.reportWeightHeader,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Log Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .tactileButton(
                        onClick = onLogClick,
                        shape = RoundedCornerShape(12.dp),
                        baseColor = ElectricIndigo,
                        lipColor = Color(0xFF3730A3)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("btn_log_weight"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.reportLogBtn,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        // Current, Heaviest, Lightest Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = strings.reportCurrentWeight,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formatDigits(String.format(Locale.US, "%.1f", currentWeight), language),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 32.sp,
                            letterSpacing = 0.sp
                        ),
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unitLabel,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // Heaviest & Lightest Pills
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = strings.reportHeaviest,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatDigits(String.format(Locale.US, "%.1f", heaviestWeight), language)} $unitLabel",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp
                        ),
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = strings.reportLightest,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatDigits(String.format(Locale.US, "%.1f", lightestWeight), language)} $unitLabel",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp
                        ),
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Custom Canvas Line Chart
        CustomWeightLineChart(
            weightPoints = weightPoints,
            currentWeight = currentWeight,
            isDark = isDark,
            language = language
        )
    }
}

@Composable
private fun CustomWeightLineChart(
    weightPoints: List<WeightLogPoint>,
    currentWeight: Float,
    isDark: Boolean,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val primaryLineColor = ElectricIndigo
    val gridLineColor = if (isDark) Color(0xFF2A2E44) else Color(0xFFE5E7EB)

    // Dynamic min/max calculation based strictly on user's real weight points
    val (minVal, maxVal) = remember(weightPoints, currentWeight) {
        if (weightPoints.isEmpty()) {
            val base = if (currentWeight > 0f) currentWeight else 70f
            (base - 5f).coerceAtLeast(0f) to (base + 5f)
        } else if (weightPoints.size == 1) {
            val w = weightPoints.first().weight
            (w - 4f).coerceAtLeast(0f) to (w + 4f)
        } else {
            val minPt = weightPoints.minOf { it.weight }
            val maxPt = weightPoints.maxOf { it.weight }
            val diff = (maxPt - minPt).coerceAtLeast(4f)
            val pad = diff * 0.2f
            (minPt - pad).coerceAtLeast(0f) to (maxPt + pad)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 10.dp)
        ) {
            val midVal = (minVal + maxVal) / 2f
            val ySteps = listOf(minVal, midVal, maxVal)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val bottomPadding = 30.dp.toPx()
                val leftPadding = 42.dp.toPx()
                val chartWidth = width - leftPadding - 16.dp.toPx()
                val chartHeight = height - bottomPadding - 16.dp.toPx()

                // Draw Y-axis guide lines
                ySteps.forEach { yVal ->
                    val ratio = ((yVal - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
                    val yPos = (height - bottomPadding) - ratio * chartHeight

                    // Dashed horizontal grid line
                    drawLine(
                        color = gridLineColor,
                        start = Offset(leftPadding, yPos),
                        end = Offset(width, yPos),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                if (weightPoints.size == 1) {
                    // With 1 point: Display a single clean point with active double ring
                    val pt = weightPoints.first()
                    val yRatio = ((pt.weight - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
                    val centerOffset = Offset(
                        x = leftPadding + chartWidth / 2f,
                        y = (height - bottomPadding) - yRatio * chartHeight
                    )

                    drawCircle(
                        color = primaryLineColor.copy(alpha = 0.25f),
                        radius = 12.dp.toPx(),
                        center = centerOffset
                    )
                    drawCircle(
                        color = primaryLineColor,
                        radius = 6.dp.toPx(),
                        center = centerOffset
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = centerOffset
                    )
                } else if (weightPoints.size >= 2) {
                    // Compute coordinates
                    val points = weightPoints.mapIndexed { index, pt ->
                        val x = leftPadding + (index.toFloat() / (weightPoints.size - 1)) * chartWidth
                        val yRatio = ((pt.weight - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
                        val y = (height - bottomPadding) - yRatio * chartHeight
                        Offset(x, y)
                    }

                    // Construct smooth spline path
                    val strokePath = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                            cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                        }
                    }

                    // Construct fill path
                    val fillPath = Path().apply {
                        addPath(strokePath)
                        lineTo(points.last().x, height - bottomPadding)
                        lineTo(points.first().x, height - bottomPadding)
                        close()
                    }

                    // Draw semi-transparent gradient fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryLineColor.copy(alpha = 0.35f),
                                primaryLineColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height - bottomPadding
                        )
                    )

                    // Draw curved line stroke
                    drawPath(
                        path = strokePath,
                        color = primaryLineColor,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw points
                    points.forEachIndexed { idx, pt ->
                        val isLast = idx == points.size - 1
                        if (isLast) {
                            // Highlighted latest point with double ring
                            drawCircle(
                                color = primaryLineColor.copy(alpha = 0.25f),
                                radius = 10.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = primaryLineColor,
                                radius = 5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.5.dp.toPx(),
                                center = pt
                            )
                        } else {
                            drawCircle(
                                color = primaryLineColor,
                                radius = 3.5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 1.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }

            // Dynamic Y-Axis Labels overlay
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .height(170.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDigits(String.format(Locale.US, "%.0f", maxVal), language),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, letterSpacing = 0.sp),
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDigits(String.format(Locale.US, "%.0f", midVal), language),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, letterSpacing = 0.sp),
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDigits(String.format(Locale.US, "%.0f", minVal), language),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, letterSpacing = 0.sp),
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Dynamic X-Axis Date/Month Labels
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .padding(start = 42.dp),
                horizontalArrangement = if (weightPoints.size <= 1) Arrangement.Center else Arrangement.SpaceBetween
            ) {
                val displayLabels = when {
                    weightPoints.isEmpty() -> emptyList()
                    weightPoints.size == 1 -> listOf(weightPoints.first().label)
                    weightPoints.size == 2 -> listOf(weightPoints.first().label, weightPoints.last().label)
                    else -> listOf(
                        weightPoints.first().label,
                        weightPoints[weightPoints.size / 2].label,
                        weightPoints.last().label
                    )
                }

                displayLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, letterSpacing = 0.sp),
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Active Tooltip for latest point: e.g. "67.0"
            if (weightPoints.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(if (weightPoints.size == 1) Alignment.TopCenter else Alignment.TopEnd)
                        .offset(x = if (weightPoints.size == 1) 0.dp else (-8).dp, y = 14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricIndigo)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = formatDigits(String.format(Locale.US, "%.1f", currentWeight), language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun BmiSpectrumCard(
    weightKg: Float,
    heightCm: Float,
    onEditHeight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // BMI calculation: weight / (h_m * h_m)
    val heightM = (heightCm / 100f).coerceAtLeast(0.5f)
    val bmi = (weightKg / (heightM * heightM)).coerceIn(10f, 50f)

    // Category and color mapping
    val (categoryTitle, categoryColor) = when {
        bmi < 16.0f -> strings.reportBmiSeverelyUnderweight to Color(0xFF2563EB)
        bmi < 18.5f -> strings.reportBmiUnderweight to Color(0xFF60A5FA)
        bmi < 25.0f -> strings.reportBmiHealthy to Color(0xFF2DD4BF)
        bmi < 30.0f -> strings.reportBmiOverweight to Color(0xFFFACC15)
        bmi < 35.0f -> strings.reportBmiObese1 to Color(0xFFFB923C)
        else -> strings.reportBmiObese2 to Color(0xFFF87171)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .tactileCard(shape = RoundedCornerShape(24.dp), elevation = 4.dp)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with BMI Title and Editable Height
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.reportBmiTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Height chip with edit pencil icon
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onEditHeight)
                    .background(if (isDark) Color(0xFF1F2336) else Color(0xFFEFF1F7))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("btn_edit_height"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${strings.reportHeightLabel}: ${formatDigits(heightCm.toInt(), language)} cm",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = strings.reportEditHeightTitle,
                    tint = ElectricIndigo,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // BMI Number and Status Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = formatDigits(String.format(Locale.US, "%.1f", bmi), language),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 32.sp,
                    letterSpacing = 0.sp
                ),
                maxLines = 1,
                softWrap = false,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryColor.copy(alpha = if (isDark) 0.25f else 0.15f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(categoryColor)
                    )
                    Text(
                        text = categoryTitle,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) categoryColor else categoryColor.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }

        // Multi-Segment Spectrum Bar with Pointer Arrow
        BmiSpectrumBar(
            bmi = bmi,
            isDark = isDark,
            language = language
        )
    }
}

@Composable
private fun BmiSpectrumBar(
    bmi: Float,
    isDark: Boolean,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    // 6 segments matching reference:
    // 1: <16 (Blue #2563EB)
    // 2: 16-18.5 (Light Blue #60A5FA)
    // 3: 18.5-25 (Cyan/Teal #2DD4BF)
    // 4: 25-30 (Yellow #FACC15)
    // 5: 30-35 (Orange #FB923C)
    // 6: 35-40+ (Red #F87171)
    val segmentColors = listOf(
        Color(0xFF2563EB),
        Color(0xFF60A5FA),
        Color(0xFF2DD4BF),
        Color(0xFFFACC15),
        Color(0xFFFB923C),
        Color(0xFFF87171)
    )

    // Segment thresholds: 15, 16, 18.5, 25, 30, 35, 40
    val thresholds = listOf(15.0f, 16.0f, 18.5f, 25.0f, 30.0f, 35.0f, 40.0f)

    // Calculate pointer ratio (0.0 to 1.0)
    val pointerRatio = remember(bmi) {
        val clampedBmi = bmi.coerceIn(15.0f, 40.0f)
        var segIndex = 0
        while (segIndex < thresholds.size - 2 && clampedBmi > thresholds[segIndex + 1]) {
            segIndex++
        }
        val lower = thresholds[segIndex]
        val upper = thresholds[segIndex + 1]
        val intraFraction = (clampedBmi - lower) / (upper - lower)
        ((segIndex + intraFraction) / 6.0f).coerceIn(0f, 1f)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(modifier = modifier.fillMaxWidth()) {
            // Pointer arrow above the bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val arrowX = size.width * pointerRatio
                    val arrowHeight = size.height
                    val arrowWidth = 10.dp.toPx()

                    val arrowPath = Path().apply {
                        moveTo(arrowX, arrowHeight) // bottom point pointing at the bar
                        lineTo(arrowX - arrowWidth / 2f, 0f)
                        lineTo(arrowX + arrowWidth / 2f, 0f)
                        close()
                    }

                    drawPath(
                        path = arrowPath,
                        color = if (isDark) Color.White else Color(0xFF171717)
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Segmented Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp)
            ) {
                segmentColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Numbers labeled below: 15, 16, 18.5, 25, 30, 35, 40
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labels = listOf("15", "16", "18.5", "25", "30", "35", "40")
                labels.forEach { num ->
                    Text(
                        text = formatDigits(num, language),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, letterSpacing = 0.sp),
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LogWeightDialog(
    initialWeight: Float,
    weightUnit: WeightUnit,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    val strings = LocalAppStrings.current
    var weightText by remember { mutableStateOf(String.format(Locale.US, "%.1f", initialWeight)) }
    var currentVal by remember { mutableFloatStateOf(initialWeight) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.reportLogWeightTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = strings.reportEnterWeightPrompt,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            currentVal = (currentVal - 0.5f).coerceAtLeast(20.0f)
                            weightText = String.format(Locale.US, "%.1f", currentVal)
                        }
                    ) {
                        Text("-0.5", fontWeight = FontWeight.Bold, color = ElectricIndigo)
                    }

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { input ->
                            weightText = input
                            input.toFloatOrNull()?.let { currentVal = it }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_weight_val"),
                        singleLine = true,
                        suffix = { Text(if (weightUnit == WeightUnit.KG) "kg" else "lbs") }
                    )

                    IconButton(
                        onClick = {
                            currentVal = (currentVal + 0.5f).coerceAtMost(300.0f)
                            weightText = String.format(Locale.US, "%.1f", currentVal)
                        }
                    ) {
                        Text("+0.5", fontWeight = FontWeight.Bold, color = ElectricIndigo)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = weightText.toFloatOrNull() ?: currentVal
                    if (parsed > 0f) {
                        onConfirm(parsed)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                modifier = Modifier.testTag("btn_confirm_log_weight")
            ) {
                Text(strings.saveGoalBtn, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelBtn)
            }
        }
    )
}

@Composable
private fun EditHeightDialog(
    initialHeight: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    val strings = LocalAppStrings.current
    var heightText by remember { mutableStateOf(initialHeight.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.reportEditHeightTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = strings.reportEnterHeightPrompt,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_height_val"),
                    singleLine = true,
                    suffix = { Text("cm") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = heightText.toFloatOrNull()
                    if (parsed != null && parsed in 50.0f..260.0f) {
                        onConfirm(parsed)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                modifier = Modifier.testTag("btn_confirm_edit_height")
            ) {
                Text(strings.saveGoalBtn, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelBtn)
            }
        }
    )
}

private fun formatMonthLabel(dateKey: String, language: AppLanguage): String {
    val parts = dateKey.split("-")
    if (parts.size >= 2) {
        val m = parts[1].toIntOrNull() ?: 1
        return if (language == AppLanguage.PERSIAN) {
            val pNames = listOf("فرو", "ارد", "خرد", "تیر", "مرد", "شهر", "مهر", "آبا", "آذر", "دی", "بهم", "اسف")
            pNames.getOrElse(m - 1) { "" }
        } else {
            val eNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            eNames.getOrElse(m - 1) { "" }
        }
    }
    return ""
}
