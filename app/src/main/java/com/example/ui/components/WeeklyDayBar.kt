package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.WorkoutDayEntity
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.tactileInset
import com.example.util.PersianDateHelper

@Composable
fun WeeklyDayBar(
    selectedDayIndex: Int,
    todayWeekIndex: Int,
    workoutDays: List<WorkoutDayEntity>,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = LocalCurrentLanguage.current
    val strings = LocalAppStrings.current
    val weekDates = PersianDateHelper.getWeekDatesInfo(language)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..7) {
            val dateInfo = weekDates.getOrNull(i - 1)
            val dayEntity = workoutDays.find { it.dayOfWeek == i }
            val isSelected = selectedDayIndex == i
            val isToday = todayWeekIndex == i
            val isTrainingDay = dayEntity?.isTrainingDay == true

            TactileDayKey(
                dayIndex = i,
                dayShortName = dateInfo?.dayOfWeekShort ?: "",
                dayNumber = dateInfo?.dayOfMonth ?: (23 + i),
                isSelected = isSelected,
                isToday = isToday,
                isTrainingDay = isTrainingDay,
                focusName = dayEntity?.focusName ?: "",
                restLabel = strings.restTag,
                workoutLabel = strings.workoutTag,
                todayLabel = strings.todayTag,
                onClick = { onSelectDay(i) }
            )
        }
    }
}

/**
 * Physical Tactile Keypad Key for days of the week:
 * - Selected: Elevated physical 3D key with Electric Indigo (Light) / Sleek Indigo (Dark) and soft shadow
 * - Training Day: Energetic #10B981 Emerald dot with luminous border
 * - Rest Day: Sunken debossed tactile inset key
 */
@Composable
private fun TactileDayKey(
    dayIndex: Int,
    dayShortName: String,
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isTrainingDay: Boolean,
    focusName: String,
    restLabel: String,
    workoutLabel: String,
    todayLabel: String,
    onClick: () -> Unit
) {
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val subtitleText = when {
        isToday && !isSelected -> todayLabel
        isTrainingDay -> if (focusName.isNotBlank()) focusName.take(8) else workoutLabel
        else -> restLabel
    }

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "key_press"
    )

    if (isSelected) {
        // Elevated Physical Key with 3D Bevel Lip
        val baseColor = if (isDark) SleekIndigo else ElectricIndigo
        val lipColor = if (isDark) Color(0xFF3131C0) else Color(0xFF3730A3)
        val shadowColor = if (isDark) SleekIndigo.copy(alpha = 0.40f) else ElectricIndigo.copy(alpha = 0.30f)

        Box(
            modifier = Modifier
                .width(58.dp)
                .height(90.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp), ambientColor = shadowColor, spotColor = shadowColor)
                .clip(RoundedCornerShape(22.dp))
                .background(lipColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(bottom = 3.dp)
                .testTag("day_pill_$dayIndex")
        ) {
            Box(
                modifier = Modifier
                    .offset(y = pressOffsetY)
                    .width(58.dp)
                    .height((90 - 3).dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(baseColor)
                    .drawBehind {
                        // Crisp top highlight
                        drawLine(
                            color = Color.White.copy(alpha = 0.35f),
                            start = Offset(x = 8f, y = 1.5f),
                            end = Offset(x = size.width - 8f, y = 1.5f),
                            strokeWidth = 2f
                        )
                    }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height((90 - 3 - 16).dp)
                ) {
                    Text(
                        text = dayShortName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Text(
                        text = formatDigits(dayNumber, language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 18.sp),
                        color = Color.White
                    )

                    // Dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isTrainingDay) Color.White else Color.White.copy(alpha = 0.4f))
                    )

                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.90f),
                        maxLines = 1
                    )
                }
            }
        }
    } else if (isTrainingDay) {
        // Training Day Key: Tactile Raised Key with Energetic #10B981 Emerald Dot & luminous border
        val keyBg = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface
        val lipBg = if (isDark) MaterialTheme.colorScheme.surfaceContainerLowest else Color(0xFFE2E8F0)
        val borderCol = if (isToday) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        }

        Box(
            modifier = Modifier
                .width(58.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(lipBg)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(bottom = 2.dp)
                .testTag("day_pill_$dayIndex")
        ) {
            Box(
                modifier = Modifier
                    .offset(y = pressOffsetY)
                    .width(58.dp)
                    .height((90 - 2).dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(keyBg)
                    .border(width = 1.dp, color = borderCol, shape = RoundedCornerShape(20.dp))
                    .drawBehind {
                        val hl = if (isDark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.60f)
                        drawLine(
                            color = hl,
                            start = Offset(x = 8f, y = 1.5f),
                            end = Offset(x = size.width - 8f, y = 1.5f),
                            strokeWidth = 2f
                        )
                    }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height((90 - 2 - 16).dp)
                ) {
                    Text(
                        text = dayShortName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = formatDigits(dayNumber, language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Energetic Emerald Dot with subtle luminous border
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), CircleShape)
                    )

                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    } else {
        // Rest Day Key: Sunken debossed appearance (tactileInset)
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(88.dp)
                .tactileInset(shape = RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .testTag("day_pill_$dayIndex"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(72.dp)
            ) {
                Text(
                    text = dayShortName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Text(
                    text = formatDigits(dayNumber, language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Subdued Neutral Dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )

                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}
