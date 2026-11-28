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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import com.example.ui.tour.LocalTourTargetReporter
import com.example.ui.tour.TourTarget
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.model.WeightUnit
import com.example.ui.components.FramedSectionHeader
import com.example.ui.components.MonthlyCalendarExpansion
import com.example.ui.components.PlannerActivityCard
import com.example.ui.components.WeeklyDayBar
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

@Composable
fun PlannerScreen(
    selectedDayIndex: Int,
    todayWeekIndex: Int,
    workoutDays: List<WorkoutDayEntity>,
    activitiesMap: Map<Int, List<ActivityEntity>>,
    weightUnit: WeightUnit,
    completedActivityIds: Set<Long> = emptySet(),
    onToggleActivityDone: ((ActivityEntity) -> Unit)? = null,
    onUpdateDayFocus: ((Int, String) -> Unit)? = null,
    onSelectDay: (Int) -> Unit,
    onToggleTrainingDay: (Int) -> Unit,
    onAddActivity: (Int) -> Unit,
    onEditActivity: (ActivityEntity) -> Unit,
    onDuplicateActivity: (ActivityEntity) -> Unit,
    onDeleteActivity: (ActivityEntity) -> Unit,
    onMoveUp: (Int, ActivityEntity) -> Unit,
    onMoveDown: (Int, ActivityEntity) -> Unit,
    onCopyPlan: (sourceDay: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val tourReporter = LocalTourTargetReporter.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val accentColor = if (isDark) EmeraldGreen else ElectricIndigo

    val dayEntity = workoutDays.find { it.dayOfWeek == selectedDayIndex }
    val isTrainingDay = dayEntity?.isTrainingDay == true
    val activities = activitiesMap[selectedDayIndex] ?: emptyList()
    val dayName = PersianDateHelper.getDayOfWeekName(selectedDayIndex, language)

    var showEditFocusDialog by rememberSaveable { mutableStateOf(false) }
    var editingFocusText by rememberSaveable { mutableStateOf("") }
    var isCalendarExpanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Header: Title, Subtitle & Action Buttons (Calendar Toggle + Copy Plan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.plannerTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.plannerSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Month Calendar Toggle Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .tactileButton(
                                onClick = { isCalendarExpanded = !isCalendarExpanded },
                                shape = RoundedCornerShape(12.dp),
                                baseColor = if (isCalendarExpanded) {
                                    if (isDark) ElectricIndigo.copy(alpha = 0.35f) else ElectricIndigo.copy(alpha = 0.15f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                },
                                lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                            .border(
                                width = if (isCalendarExpanded) 1.5.dp else 0.dp,
                                color = if (isCalendarExpanded) ElectricIndigo else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("btn_planner_month_calendar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = strings.calendarViewFullMonth,
                            tint = if (isCalendarExpanded) ElectricIndigo else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Copy Plan Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .tactileButton(
                                onClick = { onCopyPlan(selectedDayIndex) },
                                shape = RoundedCornerShape(12.dp),
                                baseColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                            .testTag("btn_copy_plan"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = strings.copyPlanToDay,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Section A: Monthly Calendar Expansion & 7-Day Week Navigation Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        if (coords.isAttached) {
                            tourReporter(TourTarget.PLANNER_DAYS_BAR, coords.boundsInRoot())
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MonthlyCalendarExpansion(
                    selectedDayIndex = selectedDayIndex,
                    todayWeekIndex = todayWeekIndex,
                    workoutDays = workoutDays,
                    activitiesMap = activitiesMap,
                    onSelectDay = onSelectDay,
                    isExpanded = isCalendarExpanded,
                    onToggleExpanded = { isCalendarExpanded = !isCalendarExpanded }
                )

                WeeklyDayBar(
                    selectedDayIndex = selectedDayIndex,
                    todayWeekIndex = todayWeekIndex,
                    workoutDays = workoutDays,
                    onSelectDay = onSelectDay
                )
            }
        }

        // Section B: Selected Day Training Status Card (Tactile Container)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .tactileCard(shape = RoundedCornerShape(20.dp), elevation = 4.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header row: Day Name + Training Status Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = if (isDark) 0.2f else 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTrainingDay) Icons.Default.FitnessCenter else Icons.Default.NightlightRound,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isTrainingDay) String.format(strings.plannerDayTrainingCount, formatDigits(activities.size, language)) else strings.plannerDayRest,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Training Day Switch Toggle
                    Switch(
                        checked = isTrainingDay,
                        onCheckedChange = { onToggleTrainingDay(selectedDayIndex) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentColor,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        modifier = Modifier.testTag("toggle_training_day")
                    )
                }

                // Custom Workout Title Section (separated into an inset panel)
                if (isTrainingDay) {
                    val hasSubtitle = dayEntity?.focusSubtitle?.isNotBlank() == true
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tactileInset(shape = RoundedCornerShape(14.dp))
                            .clickable {
                                editingFocusText = dayEntity?.focusSubtitle ?: ""
                                showEditFocusDialog = true
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag(if (hasSubtitle) "btn_edit_day_focus" else "btn_add_day_focus")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (hasSubtitle) Icons.Default.FitnessCenter else Icons.Default.Add,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (hasSubtitle) dayEntity!!.focusSubtitle else strings.setFocusCta,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (hasSubtitle) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (hasSubtitle) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = strings.editBtn,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Day Activities List with FramedSectionHeader
        item {
            FramedSectionHeader(
                title = strings.plannerDayActivitiesSection,
                badgeText = String.format(strings.plannerDayActivitiesBadge, formatDigits(activities.size, language)),
                actionText = if (isTrainingDay) strings.addActivityBtn else null,
                onActionClick = if (isTrainingDay) { { onAddActivity(selectedDayIndex) } } else null
            )
        }

        // Empty state if no activities
        if (activities.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tactileCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Embossed fitness icon well
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .tactileInset(shape = RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = strings.emptyPlanMessage,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = strings.emptyPlanCta,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Raised tactile "Add Exercise" CTA button
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .onGloballyPositioned { coords ->
                                if (coords.isAttached) {
                                    tourReporter(TourTarget.PLANNER_ADD_FAB, coords.boundsInRoot())
                                }
                            }
                            .tactileButton(
                                onClick = { onAddActivity(selectedDayIndex) },
                                shape = RoundedCornerShape(14.dp),
                                baseColor = accentColor,
                                lipColor = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)
                            )
                            .padding(horizontal = 24.dp)
                            .testTag("btn_empty_add_activity"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Text(
                                text = strings.addActivityBtn,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            itemsIndexed(activities, key = { _, act -> act.id }) { index, activity ->
                val isDone = activity.id in completedActivityIds
                PlannerActivityCard(
                    activity = activity,
                    displayIndex = index + 1,
                    weightUnit = weightUnit,
                    isDone = isDone,
                    onToggleDone = onToggleActivityDone?.let { { it(activity) } },
                    onEdit = { onEditActivity(activity) },
                    onDuplicate = { onDuplicateActivity(activity) },
                    onDelete = { onDeleteActivity(activity) },
                    onMoveUp = { onMoveUp(selectedDayIndex, activity) },
                    onMoveDown = { onMoveDown(selectedDayIndex, activity) }
                )
            }

            // Bottom Add Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .onGloballyPositioned { coords ->
                            if (coords.isAttached) {
                                tourReporter(TourTarget.PLANNER_ADD_FAB, coords.boundsInRoot())
                            }
                        }
                        .tactileButton(
                            onClick = { onAddActivity(selectedDayIndex) },
                            shape = RoundedCornerShape(14.dp),
                            baseColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                        .testTag("btn_add_activity_bottom"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = accentColor)
                        Text(
                            text = strings.addActivityBtn,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = accentColor
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog for Manual Day Focus / Title
    if (showEditFocusDialog) {
        AlertDialog(
            onDismissRequest = { showEditFocusDialog = false },
            title = {
                Text(
                    text = "${strings.editDayFocusTitle} - $dayName",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = strings.plannerDayFocusPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = editingFocusText,
                        onValueChange = { editingFocusText = it },
                        placeholder = { Text(strings.editDayFocusPlaceholder) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_day_focus")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateDayFocus?.invoke(selectedDayIndex, editingFocusText)
                        showEditFocusDialog = false
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
                TextButton(onClick = { showEditFocusDialog = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }
}

