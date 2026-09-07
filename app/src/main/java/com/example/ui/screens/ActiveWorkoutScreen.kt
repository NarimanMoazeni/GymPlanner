package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.CompletedActivityEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.model.ActivityType
import com.example.data.model.WeightUnit
import com.example.ui.components.RestTimerCard
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.SleekIndigo
import com.example.util.PersianDateHelper

@Composable
fun ActiveWorkoutScreen(
    todayWeekIndex: Int,
    workoutDay: WorkoutDayEntity?,
    activities: List<ActivityEntity>,
    completedActivities: List<CompletedActivityEntity>,
    weightUnit: WeightUnit,
    onToggleDone: (ActivityEntity) -> Unit,
    onFinishWorkout: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val todayDateInfo = PersianDateHelper.getTodayDateInfo(language)

    val completedIds = completedActivities.map { it.activityId }.toSet()
    val completedCount = activities.count { it.id in completedIds }
    val totalCount = activities.size
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    var activeCardId by remember(activities) {
        val firstIncomplete = activities.firstOrNull { it.id !in completedIds }?.id
        mutableStateOf(firstIncomplete ?: activities.firstOrNull()?.id)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = strings.activeWorkoutTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = todayDateInfo.fullDateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = onFinishWorkout,
                modifier = Modifier.testTag("btn_end_workout_top")
            ) {
                Text(
                    text = strings.finishSessionBtn,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = SleekIndigo
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Rest Timer Card
            item {
                RestTimerCard(initialSeconds = 42)
            }

            // Overall Progress Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${formatDigits(completedCount, language)} از ${formatDigits(totalCount, language)} فعالیت انجام شده",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${formatDigits((progressFraction * 100).toInt(), language)}٪",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = KineticGreen
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = KineticGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                }
            }

            // Activities Execution List
            itemsIndexed(activities, key = { _, act -> act.id }) { index, act ->
                val isDone = act.id in completedIds
                val isActive = act.id == activeCardId

                ActiveActivityItemCard(
                    activity = act,
                    index = index + 1,
                    isDone = isDone,
                    isActive = isActive,
                    weightUnit = weightUnit,
                    onSelectActive = { activeCardId = act.id },
                    onToggleDone = { onToggleDone(act) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Action Button: Complete Workout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f))
                .padding(20.dp)
        ) {
            Button(
                onClick = onFinishWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_complete_workout_bottom"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekIndigo,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Text(
                        text = strings.finishSessionBtn,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveActivityItemCard(
    activity: ActivityEntity,
    index: Int,
    isDone: Boolean,
    isActive: Boolean,
    weightUnit: WeightUnit,
    onSelectActive: () -> Unit,
    onToggleDone: () -> Unit
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val indexStr = String.format("%02d", index)
    val unitLabel = if (weightUnit == WeightUnit.KG) "کیلوگرم" else "پوند"

    val containerBg = when {
        isActive -> MaterialTheme.colorScheme.surfaceContainerHigh
        isDone -> MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.surfaceContainer
    }

    val borderModifier = when {
        isActive -> Modifier.border(1.5.dp, SleekIndigo, RoundedCornerShape(24.dp))
        isDone -> Modifier.border(1.dp, KineticGreen.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
        else -> Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(containerBg)
            .then(borderModifier)
            .clickable(onClick = onSelectActive)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Main row: Checkbox + Name + Subtitle + Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Checkbox Circle
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isDone) KineticGreen else Color.Transparent)
                        .border(
                            2.dp,
                            if (isDone) KineticGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .clickable(onClick = onToggleDone),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color(0xFF0E1116),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatDigits(indexStr, language),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isActive) SleekIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (activity.subtitle.isNotBlank()) {
                        Text(
                            text = activity.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Status tag
            if (isDone) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(KineticGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "انجام شد",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = KineticGreen
                    )
                }
            }
        }

        // Parameters Badges Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (activity.type) {
                ActivityType.WEIGHT_REPS -> {
                    Text(
                        text = "${formatDigits(activity.sets, language)} ست × ${formatDigits(activity.reps, language)} تکرار • ${formatDigits(activity.weight, language)} $unitLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ActivityType.DURATION -> {
                    Text(
                        text = "${formatDigits(activity.durationMinutes, language)} ${strings.minutesUnit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OrangeAccent
                    )
                }
                ActivityType.DISTANCE -> {
                    Text(
                        text = "${formatDigits(activity.distanceKm, language)} ${strings.kmUnit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SleekIndigo
                    )
                }
                ActivityType.SIMPLE -> {
                    Text(
                        text = strings.typeSimple,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Expanded active tracker controls
        if (isActive && !isDone) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (activity.type == ActivityType.WEIGHT_REPS) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ست‌های برنامه‌ریزی شده:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "وزن: ${formatDigits(activity.weight, language)} $unitLabel",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = KineticGreen
                        )
                    }

                    // Set chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (s in 1..activity.sets.coerceAtMost(6)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KineticGreen.copy(alpha = 0.12f))
                                    .border(1.dp, KineticGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ست $s: ${formatDigits(activity.reps, language)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KineticGreen
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onToggleDone,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = "ثبت پایان این حرکت")
                }
            }
        }
    }
}
