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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.local.entity.WorkoutSessionEntity
import com.example.data.model.ActivityType
import com.example.ui.AppScreen
import com.example.ui.components.WeeklyDayBar
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.KineticGreen
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
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header: User Greeting & Date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // User Avatar Circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.5.dp, KineticGreen.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.name.take(1),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KineticGreen
                    )
                }

                Column {
                    Text(
                        text = "سلام، ${profile.name}!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isTodayTraining) "امروز روز تمرین است" else "امروز روز استراحت و ریکاوری است",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Date Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = KineticGreen,
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

        // 7-Day Horizontal Bar
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "برنامه این هفته",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
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
                focusName = todayDayEntity?.focusName ?: "تمرین عمومی",
                subtitle = todayDayEntity?.focusSubtitle ?: "تمرکز روی برنامه امروز",
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

        // Today's Activity Checklist
        if (todayActivities.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "فعالیت‌های امروز (${formatDigits(todayActivities.size, language)})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "مشاهده جزئیات",
                        style = MaterialTheme.typography.labelMedium,
                        color = KineticGreen,
                        modifier = Modifier.clickable { onNavigate(AppScreen.PLANNER) }
                    )
                }

                todayActivities.forEachIndexed { index, act ->
                    val isDone = act.id in completedActivityIds
                    TodayActivityRow(
                        activity = act,
                        index = index + 1,
                        isDone = isDone,
                        onToggle = { onToggleActivityDone(act) }
                    )
                }
            }
        }

        // Weekly Summary Card & User Goal
        WeeklySummaryCard(
            weeklyGoal = weeklyGoal,
            totalPlannedDays = plannedTrainingDaysCount,
            onEditGoal = {
                editingGoalText = weeklyGoal
                showEditGoalDialog = true
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
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
                        text = "هدف شخصی تمرین، تغذیه یا رکوردهای ورزشی خود در این هفته را وارد کنید:",
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
                        containerColor = KineticGreen,
                        contentColor = Color(0xFF052E16)
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.5.dp, KineticGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(20.dp),
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
                            .background(KineticGreen)
                    )
                    Text(
                        text = "برنامه امروز",
                        style = MaterialTheme.typography.labelSmall,
                        color = KineticGreen
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = focusName,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
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
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KineticGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = KineticGreen
                )
            }
        }

        // Progress Bar
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${formatDigits(completedActivities, language)} از ${formatDigits(totalActivities, language)} فعالیت انجام شده",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatDigits((progress * 100).toInt(), language)}٪",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = KineticGreen
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = KineticGreen,
                trackColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        }

        // Start Workout Button
        Button(
            onClick = onStartWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_start_workout_home"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Text(
                    text = if (completedActivities > 0) "ادامه تمرین امروز" else strings.heroStartCta,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun RestDayHeroCard(
    onGoToPlanner: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "روز استراحت و ریکاوری",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "عضلات در زمان استراحت رشد و بازسازی می‌شوند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = KineticGreen
                )
            }
        }

        Button(
            onClick = onGoToPlanner,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("بررسی برنامه سایر روزها")
        }
    }
}

@Composable
private fun TodayActivityRow(
    activity: ActivityEntity,
    index: Int,
    isDone: Boolean,
    onToggle: () -> Unit
) {
    val language = LocalCurrentLanguage.current
    val strings = LocalAppStrings.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                1.dp,
                if (isDone) KineticGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggle)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Completion Checkbox Circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDone) KineticGreen else MaterialTheme.colorScheme.surfaceContainerLowest)
                .border(
                    1.5.dp,
                    if (isDone) KineticGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color(0xFF052E16),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            )

            val metaText = when (activity.type) {
                ActivityType.WEIGHT_REPS -> "${formatDigits(activity.sets, language)} ست × ${formatDigits(activity.reps, language)} تکرار • ${formatDigits(activity.weight, language)} کیلو"
                ActivityType.DURATION -> "${formatDigits(activity.durationMinutes, language)} دقیقه"
                ActivityType.DISTANCE -> "${formatDigits(activity.distanceKm, language)} کیلومتر"
                ActivityType.SIMPLE -> "فعالیت ساده"
            }

            Text(
                text = metaText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeeklySummaryCard(
    weeklyGoal: String,
    totalPlannedDays: Int,
    onEditGoal: () -> Unit
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("weekly_goal_card"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    tint = KineticGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = strings.weeklyGoalTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = onEditGoal,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .testTag("btn_edit_weekly_goal")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = strings.editGoalTitle,
                    tint = KineticGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (weeklyGoal.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(1.dp, KineticGreen.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditGoal() }
            ) {
                Text(
                    text = weeklyGoal,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditGoal() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = KineticGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.setGoalCta,
                        style = MaterialTheme.typography.bodySmall,
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
                text = "${formatDigits(totalPlannedDays, language)} روز تمرین برنامه‌ریزی شده در این هفته",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
