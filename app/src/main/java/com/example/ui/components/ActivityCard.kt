package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.model.ActivityType
import com.example.data.model.WeightUnit
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.SleekIndigo

@Composable
fun PlannerActivityCard(
    activity: ActivityEntity,
    displayIndex: Int,
    weightUnit: WeightUnit,
    isDone: Boolean = false,
    onToggleDone: (() -> Unit)? = null,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val indexStr = String.format("%02d", displayIndex)
    val unitLabel = if (weightUnit == WeightUnit.KG) "کیلوگرم" else "پوند"

    val cardAlpha by animateFloatAsState(
        targetValue = if (isDone) 0.52f else 1.0f,
        animationSpec = tween(durationMillis = 280),
        label = "card_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = cardAlpha }
            .clip(RoundedCornerShape(22.dp))
            .background(if (isDone) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = if (isDone) KineticGreen.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
            .testTag("activity_card_${activity.id}"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top row: Done Checkbox + Index & Title + Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Smooth Interactive Manual Done Checkbox / Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isDone) KineticGreen.copy(alpha = 0.22f)
                            else MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isDone) KineticGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(enabled = onToggleDone != null) { onToggleDone?.invoke() }
                        .testTag("btn_done_activity_${activity.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isDone,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = strings.activityDoneBadge,
                            tint = KineticGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (!isDone) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        )
                    }
                }

                // Up / Down arrows for smooth reordering
                Column {
                    IconButton(
                        onClick = onMoveUp,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = strings.moveUpBtn,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = strings.moveDownBtn,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            color = if (isDone) KineticGreen.copy(alpha = 0.7f) else KineticGreen
                        )
                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        if (isDone) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KineticGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = strings.activityDoneBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = KineticGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (activity.subtitle.isNotBlank()) {
                        Text(
                            text = activity.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            // Quick Actions: Edit, Duplicate, Delete
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDuplicate,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = strings.duplicateBtn,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = strings.editBtn,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = strings.deleteBtn,
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Badges / Parameters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (activity.type) {
                ActivityType.WEIGHT_REPS -> {
                    MetaBadge(
                        icon = Icons.Default.FitnessCenter,
                        text = strings.typeWeightReps,
                        iconTint = KineticGreen
                    )
                    MetaBadge(
                        icon = Icons.Default.Repeat,
                        text = "${formatDigits(activity.sets, language)} ست × ${formatDigits(activity.reps, language)} تکرار"
                    )
                    MetaBadge(
                        icon = Icons.Default.Speed,
                        text = "${formatDigits(if (activity.weight % 1.0 == 0.0) activity.weight.toLong() else activity.weight, language)} $unitLabel",
                        textColor = KineticGreen,
                        iconTint = KineticGreen
                    )
                }

                ActivityType.DURATION -> {
                    MetaBadge(
                        icon = Icons.Default.Timer,
                        text = strings.typeDuration,
                        iconTint = OrangeAccent
                    )
                    MetaBadge(
                        icon = Icons.Default.Timer,
                        text = "${formatDigits(activity.durationMinutes, language)} ${strings.minutesUnit}",
                        textColor = OrangeAccent,
                        iconTint = OrangeAccent
                    )
                }

                ActivityType.DISTANCE -> {
                    MetaBadge(
                        icon = Icons.Default.Straighten,
                        text = strings.typeDistance,
                        iconTint = SleekIndigo
                    )
                    MetaBadge(
                        icon = Icons.Default.Straighten,
                        text = "${formatDigits(activity.distanceKm, language)} ${strings.kmUnit}",
                        textColor = SleekIndigo,
                        iconTint = SleekIndigo
                    )
                }

                ActivityType.SIMPLE -> {
                    MetaBadge(
                        icon = Icons.Default.Check,
                        text = strings.typeSimple,
                        iconTint = KineticGreen
                    )
                }
            }
        }

        // Bottom mini progress row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (s in 1..4) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (s <= 2) SleekIndigo else MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                    )
                }
                Text(
                    text = "آماده اجرا",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.clickable { onEdit() }
            ) {
                Text(
                    text = strings.editBtn,
                    style = MaterialTheme.typography.labelSmall,
                    color = SleekIndigo
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = SleekIndigo,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun MetaBadge(
    icon: ImageVector,
    text: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }
    }
}
