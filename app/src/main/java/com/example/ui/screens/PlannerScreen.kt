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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.model.WeightUnit
import com.example.ui.components.PlannerActivityCard
import com.example.ui.components.WeeklyDayBar
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.KineticGreen
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

    val dayEntity = workoutDays.find { it.dayOfWeek == selectedDayIndex }
    val isTrainingDay = dayEntity?.isTrainingDay == true
    val activities = activitiesMap[selectedDayIndex] ?: emptyList()
    val dayName = PersianDateHelper.getDayOfWeekName(selectedDayIndex, language)

    var showEditFocusDialog by remember { mutableStateOf(false) }
    var editingFocusText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Header: Title & Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.plannerTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.plannerSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { onCopyPlan(selectedDayIndex) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = strings.copyPlanToDay,
                        tint = KineticGreen
                    )
                }
            }
        }

        // 7-Day Horizontal Bar
        item {
            WeeklyDayBar(
                selectedDayIndex = selectedDayIndex,
                todayWeekIndex = todayWeekIndex,
                workoutDays = workoutDays,
                onSelectDay = onSelectDay
            )
        }

        // Selected Day Status Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isTrainingDay) KineticGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTrainingDay) Icons.Default.FitnessCenter else Icons.Default.NightlightRound,
                                contentDescription = null,
                                tint = if (isTrainingDay) KineticGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isTrainingDay) "روز تمرین (${formatDigits(activities.size, language)} حرکت)" else "روز استراحت",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Training Day Switch Toggle
                    Switch(
                        checked = isTrainingDay,
                        onCheckedChange = { onToggleTrainingDay(selectedDayIndex) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF052E16),
                            checkedTrackColor = KineticGreen,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        modifier = Modifier.testTag("toggle_training_day")
                    )
                }

                if (isTrainingDay) {
                    if (dayEntity?.focusSubtitle?.isNotBlank() == true) {
                        Surface(
                            onClick = {
                                editingFocusText = dayEntity.focusSubtitle
                                showEditFocusDialog = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = BorderStroke(1.dp, KineticGreen.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_edit_day_focus")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = KineticGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = dayEntity.focusSubtitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = strings.editBtn,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        Surface(
                            onClick = {
                                editingFocusText = ""
                                showEditFocusDialog = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_add_day_focus")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = KineticGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = strings.setFocusCta,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Activities List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "حرکات این روز (${formatDigits(activities.size, language)})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isTrainingDay) {
                    TextButton(onClick = { onAddActivity(selectedDayIndex) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = KineticGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(text = strings.addActivityBtn, color = KineticGreen, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Empty state if no activities
        if (activities.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(vertical = 32.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = strings.emptyPlanMessage,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = strings.emptyPlanCta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = { onAddActivity(selectedDayIndex) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_empty_add_activity")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = strings.addActivityBtn)
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
                Button(
                    onClick = { onAddActivity(selectedDayIndex) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_add_activity_bottom"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = KineticGreen
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = KineticGreen)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = strings.addActivityBtn,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
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
                        text = "عنوانی که خودتان می‌خواهید برای تمرین این روز ثبت کنید (مانند: سینه و جلو بازو، تمرین پایین‌تنه، استراحت فعال)",
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
                        containerColor = KineticGreen,
                        contentColor = Color(0xFF052E16)
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
