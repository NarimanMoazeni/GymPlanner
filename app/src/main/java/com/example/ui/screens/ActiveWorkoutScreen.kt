package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.luminance
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
import com.example.ui.components.TactileMetricPill
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.DarkAmberGold
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.tactileButton
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset
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
        // Top App Bar: Close button + Active Workout indicator with pulsing green dot + Progress Badge
        val pulseTransition = rememberInfiniteTransition(label = "top_dot_pulse")
        val pulseAlpha by pulseTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close Button
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

            // Center: "Active Workout" text with pulsing green dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = pulseAlpha))
                )
                Text(
                    text = strings.activeWorkoutTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Right: Tactile Progress Badge (Completed / Total)
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .tactileInset(shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${formatDigits(completedCount, language)} / ${formatDigits(totalCount, language)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (completedCount == totalCount && totalCount > 0) EmeraldGreen else MaterialTheme.colorScheme.primary
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
                            text = strings.homeCompletedOfTotalActivities.format(formatDigits(completedCount, language), formatDigits(totalCount, language)),
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
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .padding(20.dp)
        ) {
            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
            val btnBase = if (isDark) EmeraldGreen else ElectricIndigo
            val btnLip = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .tactileButton(
                        onClick = onFinishWorkout,
                        shape = RoundedCornerShape(18.dp),
                        baseColor = btnBase,
                        lipColor = btnLip,
                        borderColor = btnBase.copy(alpha = 0.5f)
                    )
                    .testTag("btn_complete_workout_bottom"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = strings.finishSessionBtn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val indexStr = String.format("%02d", index)
    val unitLabel = WeightUnit.getUnitLabel(weightUnit, language == AppLanguage.PERSIAN)

    val activeColor = if (isDark) SleekIndigo else ElectricIndigo
    val cardBorder = when {
        isActive -> activeColor
        isDone -> EmeraldGreen.copy(alpha = 0.45f)
        else -> null
    }

    val cardElevation = when {
        isActive -> 8.dp
        isDone -> 2.dp
        else -> 4.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tactileCard(
                shape = RoundedCornerShape(22.dp),
                borderColor = cardBorder,
                elevation = cardElevation
            )
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
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isDone) EmeraldGreen else MaterialTheme.colorScheme.surfaceContainerLow)
                        .border(
                            2.dp,
                            if (isDone) EmeraldGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .clickable(onClick = onToggleDone),
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

                Column(modifier = Modifier.weight(1f)) {
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
                            color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE,
                                repeatDelayMillis = 1500,
                                initialDelayMillis = 2000
                            )
                        )
                    }

                    if (activity.subtitle.isNotBlank()) {
                        Text(
                            text = activity.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE,
                                repeatDelayMillis = 1500,
                                initialDelayMillis = 2000
                            )
                        )
                    }
                }
            }

            // Status tag
            if (isDone) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = strings.activityDoneBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldGreen
                    )
                }
            }
        }

        // Parameters: 3 Distinct TactileMetricPill Capsules (Weight, Sets×Reps, Exercise Type)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (activity.type) {
                ActivityType.WEIGHT_REPS -> {
                    val displayWeight = WeightUnit.formatDisplay(activity.weight, weightUnit)
                    // Pill 1: Highlighted Tactile Pill (Weight)
                    TactileMetricPill(
                        icon = Icons.Default.FitnessCenter,
                        text = "${formatDigits(displayWeight, language)} $unitLabel",
                        isHighlight = true,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1.05f)
                    )

                    // Pill 2: Inset Matte Pill (Sets × Reps)
                    val repsText = if (language == AppLanguage.PERSIAN) {
                        "${formatDigits(activity.sets, language)} ست × ${formatDigits(activity.reps, language)}"
                    } else {
                        "${formatDigits(activity.sets, language)} sets × ${formatDigits(activity.reps, language)}"
                    }
                    TactileMetricPill(
                        icon = Icons.Default.Refresh,
                        text = repsText,
                        isHighlight = false,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1.15f)
                    )

                    // Pill 3: Inset Matte Pill (Exercise Type)
                    TactileMetricPill(
                        icon = Icons.Default.FitnessCenter,
                        text = strings.typeWeightReps,
                        isHighlight = false,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1.0f)
                    )
                }
                ActivityType.DURATION -> {
                    TactileMetricPill(
                        icon = Icons.Default.Timer,
                        text = "${formatDigits(activity.durationMinutes, language)} ${strings.minutesUnit}",
                        isHighlight = true,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1.2f)
                    )
                    TactileMetricPill(
                        icon = Icons.Default.Timer,
                        text = strings.typeDuration,
                        isHighlight = false,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1f)
                    )
                }
                ActivityType.DISTANCE -> {
                    TactileMetricPill(
                        icon = Icons.Default.Straighten,
                        text = "${formatDigits(activity.distanceKm, language)} ${strings.kmUnit}",
                        isHighlight = true,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1.2f)
                    )
                    TactileMetricPill(
                        icon = Icons.Default.Straighten,
                        text = strings.typeDistance,
                        isHighlight = false,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1f)
                    )
                }
                ActivityType.SIMPLE -> {
                    TactileMetricPill(
                        icon = Icons.Default.FitnessCenter,
                        text = strings.typeSimple,
                        isHighlight = true,
                        onClick = onSelectActive,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Expanded active tracker controls
        if (isActive && !isDone) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileInset(shape = RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activity.type == ActivityType.WEIGHT_REPS) {
                    val displayWeight = WeightUnit.formatDisplay(activity.weight, weightUnit)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.activeWorkoutPlannedSets,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${strings.activeWorkoutWeightLabel.format(formatDigits(displayWeight, language), unitLabel)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldGreen
                        )
                    }

                    // High-Ergonomics Set Tracker: Large, tactile pressable tiles (min height 48.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (s in 1..activity.sets.coerceAtMost(6)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .tactileButton(
                                        onClick = onToggleDone,
                                        shape = RoundedCornerShape(12.dp),
                                        baseColor = EmeraldGreen.copy(alpha = 0.15f),
                                        lipColor = EmeraldGreen.copy(alpha = 0.35f),
                                        borderColor = EmeraldGreen.copy(alpha = 0.40f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = strings.activeWorkoutSetBadge.format(formatDigits(s, language)),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldGreen
                                    )
                                    Text(
                                        text = strings.activeWorkoutRepsBadge.format(formatDigits(activity.reps, language)),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .tactileButton(
                            onClick = onToggleDone,
                            shape = RoundedCornerShape(12.dp),
                            baseColor = MaterialTheme.colorScheme.primary,
                            lipColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text(
                            text = strings.markExerciseDoneBtn,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
