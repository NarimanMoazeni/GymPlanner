package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.luminance
import com.example.data.local.entity.ActivityEntity
import com.example.data.model.ActivityType
import com.example.data.model.WeightUnit
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.tactileButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditActivitySheet(
    sheetState: SheetState,
    initialActivity: ActivityEntity?,
    targetDayOfWeek: Int,
    weightUnit: WeightUnit,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        subtitle: String,
        type: ActivityType,
        sets: Int,
        reps: Int,
        weight: Double,
        duration: Int,
        distance: Double
    ) -> Unit
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current

    var name by remember(initialActivity) { mutableStateOf(initialActivity?.name ?: "") }
    var subtitle by remember(initialActivity) { mutableStateOf(initialActivity?.subtitle ?: "") }
    var type by remember(initialActivity) { mutableStateOf(initialActivity?.type ?: ActivityType.WEIGHT_REPS) }
    var sets by remember(initialActivity) { mutableIntStateOf(initialActivity?.sets ?: 4) }
    var reps by remember(initialActivity) { mutableIntStateOf(initialActivity?.reps ?: 10) }
    var weight by remember(initialActivity, weightUnit) {
        val initialKg = initialActivity?.weight ?: 25.0
        mutableDoubleStateOf(WeightUnit.fromKg(initialKg, weightUnit))
    }
    var duration by remember(initialActivity) { mutableIntStateOf(initialActivity?.durationMinutes ?: 15) }
    var distance by remember(initialActivity) { mutableDoubleStateOf(initialActivity?.distanceKm ?: 3.0) }
    var nameError by remember { mutableStateOf<String?>(null) }

    val unitLabel = if (weightUnit == WeightUnit.KG) "kg" else "lbs"
    val unitSuffix = if (weightUnit == WeightUnit.KG) "کیلو" else "پوند"
    val weightStep = if (weightUnit == WeightUnit.KG) 2.5 else 5.0
    val weightPresets = if (weightUnit == WeightUnit.KG) {
        listOf("۱۵ $unitSuffix" to 15.0, "۲۰ $unitSuffix" to 20.0, "۲۵ $unitSuffix" to 25.0, "۳۰ $unitSuffix" to 30.0)
    } else {
        listOf("۳۰ $unitSuffix" to 30.0, "۴۵ $unitSuffix" to 45.0, "۵۵ $unitSuffix" to 55.0, "۷۰ $unitSuffix" to 70.0)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 48.dp, height = 5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (initialActivity == null) strings.modalTitleAdd else strings.modalTitleEdit,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.modalSubtitle,
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
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = KineticGreen
                    )
                }
            }

            // Step 1: Activity Name
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.stepActivityName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = strings.stepIndicator,
                        style = MaterialTheme.typography.labelSmall,
                        color = KineticGreen
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    placeholder = { Text(strings.activityNamePlaceholder) },
                    singleLine = true,
                    isError = nameError != null,
                    trailingIcon = {
                        if (name.isNotEmpty()) {
                            IconButton(onClick = { name = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_activity_name"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedBorderColor = KineticGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                nameError?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Step 2: Activity Category Pills (2x2 grid)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = strings.stepActivityType,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TypePillButton(
                            title = strings.typeWeightReps,
                            icon = Icons.Default.FitnessCenter,
                            isSelected = type == ActivityType.WEIGHT_REPS,
                            modifier = Modifier.weight(1f),
                            onClick = { type = ActivityType.WEIGHT_REPS }
                        )

                        TypePillButton(
                            title = strings.typeDuration,
                            icon = Icons.Default.Timer,
                            isSelected = type == ActivityType.DURATION,
                            modifier = Modifier.weight(1f),
                            onClick = { type = ActivityType.DURATION }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TypePillButton(
                            title = strings.typeDistance,
                            icon = Icons.Default.Straighten,
                            isSelected = type == ActivityType.DISTANCE,
                            modifier = Modifier.weight(1f),
                            onClick = { type = ActivityType.DISTANCE }
                        )

                        TypePillButton(
                            title = strings.typeSimple,
                            icon = Icons.Default.CheckCircle,
                            isSelected = type == ActivityType.SIMPLE,
                            modifier = Modifier.weight(1f),
                            onClick = { type = ActivityType.SIMPLE }
                        )
                    }
                }
            }

            // Step 3: Relevant Numerical Steppers based on selected type
            when (type) {
                ActivityType.WEIGHT_REPS -> {
                    // Sets
                    NumericStepperCard(
                        title = strings.setsLabel,
                        value = sets,
                        onValueChange = { sets = it.toInt().coerceAtLeast(1) },
                        step = 1.0,
                        minValue = 1.0,
                        maxValue = 30.0,
                        dotColor = KineticGreen,
                        quickPresets = listOf("۳" to 3.0, "۴" to 4.0, "۵" to 5.0),
                        presetLabel = strings.presetsLabel,
                        testTag = "stepper_sets"
                    )

                    // Reps
                    NumericStepperCard(
                        title = strings.repsLabel,
                        value = reps,
                        onValueChange = { reps = it.toInt().coerceAtLeast(1) },
                        step = 1.0,
                        minValue = 1.0,
                        maxValue = 100.0,
                        dotColor = SleekIndigo,
                        accentValueColor = SleekIndigo,
                        quickPresets = listOf("۸" to 8.0, "۱۰" to 10.0, "۱۲" to 12.0, "۱۵" to 15.0),
                        presetLabel = strings.quickLabel,
                        testTag = "stepper_reps"
                    )

                    // Weight
                    NumericStepperCard(
                        title = strings.weightLabel,
                        value = weight,
                        onValueChange = { weight = it.coerceAtLeast(0.0) },
                        step = weightStep,
                        minValue = 0.0,
                        maxValue = if (weightUnit == WeightUnit.KG) 500.0 else 1100.0,
                        unitSuffix = unitLabel,
                        dotColor = OrangeAccent,
                        accentValueColor = OrangeAccent,
                        quickPresets = weightPresets,
                        presetLabel = strings.commonLabel,
                        testTag = "stepper_weight"
                    )
                }

                ActivityType.DURATION -> {
                    NumericStepperCard(
                        title = strings.durationLabel,
                        value = duration,
                        onValueChange = { duration = it.toInt().coerceAtLeast(1) },
                        step = 5.0,
                        minValue = 1.0,
                        maxValue = 300.0,
                        unitSuffix = strings.minutesUnit,
                        dotColor = OrangeAccent,
                        accentValueColor = OrangeAccent,
                        quickPresets = listOf("۵" to 5.0, "۱۰" to 10.0, "۱۵" to 15.0, "۲۰" to 20.0, "۳۰" to 30.0),
                        presetLabel = strings.quickLabel,
                        testTag = "stepper_duration"
                    )
                }

                ActivityType.DISTANCE -> {
                    NumericStepperCard(
                        title = strings.distanceLabel,
                        value = distance,
                        onValueChange = { distance = it.coerceAtLeast(0.1) },
                        step = 0.5,
                        minValue = 0.1,
                        maxValue = 100.0,
                        unitSuffix = strings.kmUnit,
                        dotColor = SleekIndigo,
                        accentValueColor = SleekIndigo,
                        quickPresets = listOf("۱" to 1.0, "۲" to 2.0, "۳" to 3.0, "۵" to 5.0, "۱۰" to 10.0),
                        presetLabel = strings.quickLabel,
                        testTag = "stepper_distance"
                    )
                }

                ActivityType.SIMPLE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = strings.simpleNotice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Intelligent Context Notice
            val noticeText = when (type) {
                ActivityType.WEIGHT_REPS -> strings.strengthNotice
                ActivityType.DURATION -> strings.durationNotice
                ActivityType.DISTANCE -> strings.distanceNotice
                ActivityType.SIMPLE -> strings.simpleNotice
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = KineticGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = noticeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            // Action Buttons (Save & Cancel)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                val btnBase = if (isDark) SleekIndigo else ElectricIndigo
                val btnLip = if (isDark) Color(0xFF3131C0) else Color(0xFF3730A3)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .tactileButton(
                            onClick = {
                                if (name.isBlank()) {
                                    nameError = strings.errEnterName
                                } else {
                                    val canonicalWeightKg = WeightUnit.toKg(weight, weightUnit)
                                    onSave(name, subtitle, type, sets, reps, canonicalWeightKg, duration, distance)
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            baseColor = btnBase,
                            lipColor = btnLip,
                            borderColor = btnBase.copy(alpha = 0.5f)
                        )
                        .testTag("btn_save_activity"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Text(
                            text = strings.saveToPlanBtn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .tactileButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(16.dp),
                            baseColor = MaterialTheme.colorScheme.surface,
                            lipColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.cancelBtn,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TypePillButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val activeColor = if (isDark) SleekIndigo else ElectricIndigo

    if (isSelected) {
        val lip = if (isDark) Color(0xFF3131C0) else Color(0xFF3730A3)
        Box(
            modifier = modifier
                .height(46.dp)
                .tactileButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(14.dp),
                    baseColor = activeColor,
                    lipColor = lip,
                    borderColor = activeColor.copy(alpha = 0.5f)
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .height(46.dp)
                .tactileButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(14.dp),
                    baseColor = MaterialTheme.colorScheme.surface,
                    lipColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
