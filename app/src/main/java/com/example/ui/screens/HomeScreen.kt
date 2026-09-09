package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.local.entity.WorkoutSessionEntity
import com.example.data.model.ActivityType
import com.example.data.model.WeightUnit
import com.example.ui.AppScreen
import com.example.ui.components.FramedSectionHeader
import com.example.ui.components.WeeklyDayBar
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.tactileButton
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset
import com.example.util.PersianDateHelper

@Composable
fun HomeScreen(
    profile: UserProfileEntity,
    workoutDays: List<WorkoutDayEntity>,
    activitiesMap: Map<Int, List<ActivityEntity>>,
    todayWeekIndex: Int,
    todaySession: WorkoutSessionEntity?,
    completedActivityIds: Set<Long>,
    weeklyGoal: String = "",
    weightUnit: WeightUnit = WeightUnit.KG,
    onUpdateWeeklyGoal: ((String) -> Unit)? = null,
    onToggleActivityDone: (ActivityEntity) -> Unit,
    onStartWorkout: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onSelectDayInPlanner: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val todayDateInfo = PersianDateHelper.getTodayDateInfo(language)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val accentColor = if (isDark) EmeraldGreen else ElectricIndigo

    val todayDayEntity = workoutDays.find { it.dayOfWeek == todayWeekIndex }
    val isTodayTraining = todayDayEntity?.isTrainingDay == true
    val todayActivities = activitiesMap[todayWeekIndex] ?: emptyList()
    val completedCount = todayActivities.count { it.id in completedActivityIds }
    val progressFraction = if (todayActivities.isNotEmpty()) completedCount.toFloat() / todayActivities.size else 0f

    // Total completed days this week calculation
    val plannedTrainingDaysCount = workoutDays.count { it.isTrainingDay }

    var showEditGoalDialog by remember { mutableStateOf(false) }
    var editingGoalText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Identity Bento Card: User Avatar, Greeting, Training Status Dot & Date
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 52.dp Squircle Container (dual-color rim: ElectricIndigo + AmberGold) with athletic icon
                    val rimBrush = Brush.linearGradient(
                        listOf(ElectricIndigo, AmberGold)
                    )
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(accentColor.copy(alpha = if (isDark) 0.20f else 0.12f))
                            .border(2.dp, rimBrush, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = profile.name,
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = String.format(strings.homeGreetingFormat, profile.name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Micro-dot indicator for today's status (Training vs Rest)
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isTodayTraining) EmeraldGreen else MaterialTheme.colorScheme.outline)
                            )
                            Text(
                                text = if (isTodayTraining) strings.homeStatusTraining else strings.homeStatusRest,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Date Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = todayDateInfo.dayOfWeekName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 7-Day Horizontal Bar Section
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FramedSectionHeader(
                title = strings.homeWeeklySectionTitle,
                badgeText = String.format(strings.homeWeeklyBadge, formatDigits(plannedTrainingDaysCount, language)),
                actionText = strings.homeViewCalendar,
                onActionClick = { onNavigate(AppScreen.PLANNER) }
            )
            WeeklyDayBar(
                selectedDayIndex = todayWeekIndex,
                todayWeekIndex = todayWeekIndex,
                workoutDays = workoutDays,
                onSelectDay = { day ->
                    onSelectDayInPlanner(day)
                    onNavigate(AppScreen.PLANNER)
                }
            )
        }

        // Hero Card: Today's Plan (or Rest Day)
        if (isTodayTraining) {
            TodayTrainingHeroCard(
                focusName = todayDayEntity?.focusName ?: strings.homeDefaultFocusName,
                subtitle = todayDayEntity?.focusSubtitle ?: strings.homeDefaultFocusSubtitle,
                totalActivities = todayActivities.size,
                completedActivities = completedCount,
                progress = progressFraction,
                onStartWorkout = onStartWorkout
            )
        } else {
            RestDayHeroCard(
                onGoToPlanner = { onNavigate(AppScreen.PLANNER) }
            )
        }

        // Today's Activity Checklist Section
        if (todayActivities.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FramedSectionHeader(
                    title = strings.homeTodayActivitiesSection,
                    badgeText = String.format(strings.homeTodayActivitiesBadge, formatDigits(todayActivities.size, language)),
                    actionText = strings.homeViewDetails,
                    onActionClick = { onNavigate(AppScreen.PLANNER) }
                )

                todayActivities.forEachIndexed { index, act ->
                    val isDone = act.id in completedActivityIds
                    TodayActivityRow(
                        activity = act,
                        index = index + 1,
                        isDone = isDone,
                        weightUnit = weightUnit,
                        onToggle = { onToggleActivityDone(act) }
                    )
                }
            }
        }

        // Athletic Mission Card (Weekly Summary & User Goal)
        WeeklySummaryCard(
            weeklyGoal = weeklyGoal,
            totalPlannedDays = plannedTrainingDaysCount,
            onEditGoal = {
                editingGoalText = weeklyGoal
                showEditGoalDialog = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showEditGoalDialog) {
        AlertDialog(
            onDismissRequest = { showEditGoalDialog = false },
            title = {
                Text(
                    text = strings.editGoalTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = strings.homeWeeklyGoalDialogPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = editingGoalText,
                        onValueChange = { editingGoalText = it },
                        placeholder = { Text(strings.weeklyGoalPlaceholder) },
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_weekly_goal")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateWeeklyGoal?.invoke(editingGoalText)
                        showEditGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.saveGoalBtn, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalDialog = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }
}

@Composable
private fun TodayTrainingHeroCard(
    focusName: String,
    subtitle: String,
    totalActivities: Int,
    completedActivities: Int,
    progress: Float,
    onStartWorkout: () -> Unit
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val activeBrand = if (isDark) SleekIndigo else ElectricIndigo
    val emerald = EmeraldGreen

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tactileCard(
                shape = RoundedCornerShape(24.dp),
                borderColor = activeBrand.copy(alpha = 0.40f),
                elevation = 8.dp
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(emerald)
                    )
                    Text(
                        text = strings.homeTodayProgramBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = emerald
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = focusName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(activeBrand.copy(alpha = if (isDark) 0.20f else 0.12f))
                    .border(1.dp, activeBrand.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = activeBrand,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Rounded Capsule Pill Progress Bar with bold percentage badge
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(strings.homeCompletedOfTotalActivities, formatDigits(completedActivities, language), formatDigits(totalActivities, language)),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Bold percentage badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(emerald.copy(alpha = 0.15f))
                        .border(1.dp, emerald.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    val percentStr = if (language == AppLanguage.PERSIAN) "${formatDigits((progress * 100).toInt(), language)}٪" else "${formatDigits((progress * 100).toInt(), language)}%"
                    Text(
                        text = percentStr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = emerald
                    )
                }
            }

            // Capsule track & fill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(emerald)
                )
            }
        }

        // Start Workout Button: Raised Tactile Button (54dp height)
        val btnBase = if (isDark) emerald else activeBrand
        val btnLip = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .tactileButton(
                    onClick = onStartWorkout,
                    shape = RoundedCornerShape(16.dp),
                    baseColor = btnBase,
                    lipColor = btnLip,
                    borderColor = btnBase.copy(alpha = 0.5f)
                )
                .testTag("btn_start_workout_home"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = if (completedActivities > 0) strings.homeContinueWorkout else strings.heroStartCta,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun RestDayHeroCard(
    onGoToPlanner: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tactileCard(
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = strings.homeRestHeroTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.homeRestHeroDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .tactileButton(
                    onClick = onGoToPlanner,
                    shape = RoundedCornerShape(14.dp),
                    baseColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = strings.homeCheckOtherDays,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TodayActivityRow(
    activity: ActivityEntity,
    index: Int,
    isDone: Boolean,
    weightUnit: WeightUnit,
    onToggle: () -> Unit
) {
    val language = LocalCurrentLanguage.current
    val strings = LocalAppStrings.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val activeColor = if (isDark) SleekIndigo else ElectricIndigo

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tactileCard(
                shape = RoundedCornerShape(18.dp),
                elevation = if (isDone) 2.dp else 4.dp,
                borderColor = if (isDone) EmeraldGreen.copy(alpha = 0.45f) else null
            )
            .clickable(onClick = onToggle)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Completion Checkbox Circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDone) EmeraldGreen else MaterialTheme.colorScheme.surfaceContainerLowest)
                .border(
                    1.5.dp,
                    if (isDone) EmeraldGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Details
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                ),
                color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            )

            val metaText = when (activity.type) {
                ActivityType.WEIGHT_REPS -> {
                    val displayWeight = WeightUnit.formatDisplay(activity.weight, weightUnit)
                    val unitLabel = WeightUnit.getUnitLabel(weightUnit, language == AppLanguage.PERSIAN)
                    val setsReps = String.format(strings.activeWorkoutSetAndReps, formatDigits(activity.sets, language), formatDigits(activity.reps, language))
                    "$setsReps • ${formatDigits(displayWeight, language)} $unitLabel"
                }
                ActivityType.DURATION -> "${formatDigits(activity.durationMinutes, language)} ${strings.minutesUnit}"
                ActivityType.DISTANCE -> "${formatDigits(activity.distanceKm, language)} ${strings.kmUnit}"
                ActivityType.SIMPLE -> strings.typeSimple
            }

            Text(
                text = metaText,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Athletic Mission Card (Weekly Summary & User Goal):
 * - Encloses the weekly goal text inside a sunken inset panel (tactileInset)
 * - Features an edit badge and athletic summary stats
 */
@Composable
private fun WeeklySummaryCard(
    weeklyGoal: String,
    totalPlannedDays: Int,
    onEditGoal: () -> Unit
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val accentColor = if (isDark) EmeraldGreen else ElectricIndigo

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
            .padding(16.dp)
            .testTag("weekly_goal_card"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrackChanges,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = strings.weeklyGoalTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Edit Goal Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .tactileButton(
                        onClick = onEditGoal,
                        shape = RoundedCornerShape(10.dp),
                        baseColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                    .testTag("btn_edit_weekly_goal"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = strings.editGoalTitle,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Sunken inset panel (tactileInset) for the goal readout
        if (weeklyGoal.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileInset(shape = RoundedCornerShape(14.dp))
                    .clickable { onEditGoal() }
                    .padding(14.dp)
            ) {
                Text(
                    text = weeklyGoal,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileInset(shape = RoundedCornerShape(14.dp))
                    .clickable { onEditGoal() }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.setGoalCta,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(strings.homePlannedDaysSummary, formatDigits(totalPlannedDays, language)),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
