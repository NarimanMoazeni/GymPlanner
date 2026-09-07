package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.WorkoutDayEntity
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.IndigoPurple
import com.example.ui.theme.KineticGreen
import com.example.util.FormattedDateInfo
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
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..7) {
            val dateInfo = weekDates.getOrNull(i - 1)
            val dayEntity = workoutDays.find { it.dayOfWeek == i }
            val isSelected = selectedDayIndex == i
            val isToday = todayWeekIndex == i
            val isTrainingDay = dayEntity?.isTrainingDay == true

            DayPill(
                dayIndex = i,
                dayShortName = dateInfo?.dayOfWeekShort ?: "",
                dayFullName = dateInfo?.dayOfWeekName ?: "",
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

@Composable
private fun DayPill(
    dayIndex: Int,
    dayShortName: String,
    dayFullName: String,
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

    val containerBg = when {
        isSelected -> SleekIndigo
        isToday -> MaterialTheme.colorScheme.surfaceContainerHigh
        isTrainingDay -> MaterialTheme.colorScheme.surfaceContainer
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }

    val textColor = when {
        isSelected -> Color.White
        isToday -> MaterialTheme.colorScheme.onSurface
        isTrainingDay -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val subtitleText = when {
        isToday && !isSelected -> todayLabel
        isTrainingDay -> if (focusName.isNotBlank()) focusName.take(8) else workoutLabel
        else -> restLabel
    }

    val dotColor = when {
        isSelected -> Color.White
        isTrainingDay -> KineticGreen
        isToday -> SleekIndigo
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }

    Column(
        modifier = Modifier
            .width(56.dp)
            .height(86.dp)
            .scale(if (isSelected) 1.04f else 1.0f)
            .clip(RoundedCornerShape(24.dp))
            .background(containerBg)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) SleekIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("day_pill_$dayIndex"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Day short name (e.g. ش, ی, د / Sat, Sun)
        Text(
            text = dayShortName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        // Day Date Number (e.g. 12, 13, 14)
        Text(
            text = formatDigits(dayNumber, language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
            color = textColor
        )

        // Status Indicator Dot or Mini Icon
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        // Subtitle (Rest / Workout / Split Name)
        Text(
            text = subtitleText,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = if (isSelected) Color.White.copy(alpha = 0.9f) else textColor.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}
