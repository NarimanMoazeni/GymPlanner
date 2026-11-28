package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset
import com.example.util.PersianDateHelper
import java.util.Calendar

@Composable
fun MonthlyCalendarExpansion(
    selectedDayIndex: Int,
    todayWeekIndex: Int,
    workoutDays: List<WorkoutDayEntity>,
    activitiesMap: Map<Int, List<ActivityEntity>>,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    isExpanded: Boolean? = null,
    onToggleExpanded: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    var internalExpanded by rememberSaveable { mutableStateOf(false) }
    val expanded = isExpanded ?: internalExpanded
    val toggle = onToggleExpanded ?: { internalExpanded = !internalExpanded }

    Column(modifier = modifier.fillMaxWidth()) {
        // Toggle Action Button above the 7-day bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title ?: strings.homeWeeklySectionTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tactile Action Chip
            val interactionSource = remember { MutableInteractionSource() }
            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(interactionSource = interactionSource, indication = null) {
                        toggle()
                    }
                    .background(
                        if (expanded) {
                            if (isDark) ElectricIndigo.copy(alpha = 0.25f) else ElectricIndigo.copy(alpha = 0.12f)
                        } else {
                            if (isDark) Color(0xFF1E2130) else Color(0xFFEDEEF6)
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (expanded) ElectricIndigo else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("btn_toggle_month_calendar"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (expanded) ElectricIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (expanded) strings.calendarHideMonth else strings.calendarViewFullMonth,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                    color = if (expanded) ElectricIndigo else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Full Month Expandable Grid
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp)
                    .tactileCard(shape = RoundedCornerShape(20.dp), elevation = 4.dp)
                    .padding(14.dp)
            ) {
                MonthlyCalendarGrid(
                    selectedDayIndex = selectedDayIndex,
                    todayWeekIndex = todayWeekIndex,
                    workoutDays = workoutDays,
                    activitiesMap = activitiesMap,
                    onSelectDay = { dayOfWeek ->
                        onSelectDay(dayOfWeek)
                    }
                )
            }
        }
    }
}

@Composable
fun MonthlyCalendarGrid(
    selectedDayIndex: Int,
    todayWeekIndex: Int,
    workoutDays: List<WorkoutDayEntity>,
    activitiesMap: Map<Int, List<ActivityEntity>>,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = LocalCurrentLanguage.current
    val strings = LocalAppStrings.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Navigation state: current calendar month offset
    var monthOffset by rememberSaveable { mutableIntStateOf(0) }

    val todayCal = remember { Calendar.getInstance() }
    val (todayPYear, todayPMonth, todayPDay) = remember {
        PersianDateHelper.gregorianToPersian(
            todayCal.get(Calendar.YEAR),
            todayCal.get(Calendar.MONTH) + 1,
            todayCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Gregorian displayCal for English mode
    val displayCal = remember(monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
        }
    }
    val currentYear = displayCal.get(Calendar.YEAR)
    val currentMonth = displayCal.get(Calendar.MONTH) + 1

    // Persian year & month calculation with offset
    val (displayPYear, displayPMonth) = remember(monthOffset, todayPYear, todayPMonth) {
        val totalMonths = (todayPYear * 12 + (todayPMonth - 1)) + monthOffset
        val yr = totalMonths / 12
        val mo = (totalMonths % 12) + 1
        yr to mo
    }

    // Days in current display month
    val daysInMonth = if (language == AppLanguage.PERSIAN) {
        PersianDateHelper.getDaysInPersianMonth(displayPYear, displayPMonth)
    } else {
        displayCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Day of week index for day 1 (1=Sat..7=Fri)
    val firstDayWeekIndex = if (language == AppLanguage.PERSIAN) {
        val (gy, gm, gd) = PersianDateHelper.persianToGregorian(displayPYear, displayPMonth, 1)
        val c = Calendar.getInstance().apply { set(gy, gm - 1, gd) }
        PersianDateHelper.calendarDayToWeekIndex(c.get(Calendar.DAY_OF_WEEK))
    } else {
        val firstDayCal = Calendar.getInstance().apply {
            timeInMillis = displayCal.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        PersianDateHelper.calendarDayToWeekIndex(firstDayCal.get(Calendar.DAY_OF_WEEK))
    }

    val leadingEmptyCells = firstDayWeekIndex - 1

    // Today info
    val isCurrentDisplayMonth = if (language == AppLanguage.PERSIAN) {
        monthOffset == 0
    } else {
        todayCal.get(Calendar.YEAR) == currentYear && (todayCal.get(Calendar.MONTH) + 1) == currentMonth
    }
    val todayDayOfMonth = if (language == AppLanguage.PERSIAN) {
        if (isCurrentDisplayMonth) todayPDay else -1
    } else {
        if (isCurrentDisplayMonth) todayCal.get(Calendar.DAY_OF_MONTH) else -1
    }

    // Selected day of month within this calendar view
    var selectedCalendarDay by rememberSaveable {
        mutableIntStateOf(if (isCurrentDisplayMonth) todayDayOfMonth else 1)
    }

    // Month Title text: e.g. "شهریور ۱۴۰۵" or "September 2026"
    val monthTitle = remember(monthOffset, displayPYear, displayPMonth, currentYear, currentMonth, language) {
        if (language == AppLanguage.PERSIAN) {
            val mName = PersianDateHelper.PERSIAN_MONTH_NAMES.getOrElse(displayPMonth - 1) { "" }
            "$mName ${formatDigits(displayPYear, language)}"
        } else {
            val eMonthNames = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            "${eMonthNames.getOrElse(currentMonth - 1) { "" }} $currentYear"
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Month Navigation Bar: [ ◀ 2026/09 ▶ ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Month Button
            IconButton(
                onClick = { monthOffset-- },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1F2336) else Color(0xFFEFF1F7))
                    .testTag("btn_cal_prev_month")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Month/Year Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = monthTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (monthOffset != 0) {
                    Text(
                        text = strings.calendarTodayBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricIndigo,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricIndigo.copy(alpha = 0.12f))
                            .clickable { monthOffset = 0 }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Next Month Button
            IconButton(
                onClick = { monthOffset++ },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1F2336) else Color(0xFFEFF1F7))
                    .testTag("btn_cal_next_month")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Day Headers: S M T W T F S (or Persian: ش ی د س چ پ ج)
        val dayHeaders = if (language == AppLanguage.PERSIAN) {
            listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
        } else {
            listOf("S", "M", "T", "W", "T", "F", "S")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dayHeaders.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Full 5-6 week calendar grid
        val totalCells = leadingEmptyCells + daysInMonth
        val rows = (totalCells + 6) / 7

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (r in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (c in 0 until 7) {
                        val cellIndex = r * 7 + c
                        val dayNumber = cellIndex - leadingEmptyCells + 1

                        if (dayNumber in 1..daysInMonth) {
                            // Calculate day of week index (1=Sat..7=Fri)
                            val dayOfWeek = if (language == AppLanguage.PERSIAN) {
                                ((firstDayWeekIndex - 1 + (dayNumber - 1)) % 7) + 1
                            } else {
                                val dayCal = Calendar.getInstance().apply {
                                    timeInMillis = displayCal.timeInMillis
                                    set(Calendar.DAY_OF_MONTH, dayNumber)
                                }
                                PersianDateHelper.calendarDayToWeekIndex(dayCal.get(Calendar.DAY_OF_WEEK))
                            }

                            // Check if day has planned workouts
                            val dayEntity = workoutDays.find { it.dayOfWeek == dayOfWeek }
                            val hasActivities = activitiesMap[dayOfWeek]?.isNotEmpty() == true
                            val hasPlannedWorkout = (dayEntity?.isTrainingDay == true) && hasActivities

                            val isSelected = (selectedCalendarDay == dayNumber) ||
                                    (isCurrentDisplayMonth && selectedDayIndex == dayOfWeek && selectedCalendarDay == dayNumber)
                            val isToday = isCurrentDisplayMonth && (dayNumber == todayDayOfMonth)

                            CalendarDayCell(
                                dayNumber = dayNumber,
                                isSelected = isSelected,
                                isToday = isToday,
                                hasPlannedWorkout = hasPlannedWorkout,
                                language = language,
                                onClick = {
                                    selectedCalendarDay = dayNumber
                                    onSelectDay(dayOfWeek)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            // Empty placeholder cell
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Legend indicator below grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(KineticGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = strings.calendarDaysWithWorkout,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasPlannedWorkout: Boolean,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(
                when {
                    isSelected -> ElectricIndigo
                    isToday -> if (isDark) ElectricIndigo.copy(alpha = 0.2f) else ElectricIndigo.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                color = if (isToday && !isSelected) ElectricIndigo else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = formatDigits(dayNumber, language),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp,
                    letterSpacing = 0.sp
                ),
                maxLines = 1,
                softWrap = false,
                color = when {
                    isSelected -> Color.White
                    isToday -> ElectricIndigo
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Athletic green dot for days with planned workouts
            if (hasPlannedWorkout) {
                Box(
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else KineticGreen)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
