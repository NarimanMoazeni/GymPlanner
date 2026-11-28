package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.theme.KineticGreen
import com.example.util.PersianDateHelper

@Composable
fun CopyDayPlanDialog(
    sourceDayOfWeek: Int,
    onDismiss: () -> Unit,
    onConfirm: (targetDayOfWeek: Int) -> Unit
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val sourceDayName = PersianDateHelper.getDayOfWeekName(sourceDayOfWeek, language)

    var selectedTargetDay by rememberSaveable {
        mutableIntStateOf(if (sourceDayOfWeek == 2) 4 else 2)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = KineticGreen)
                Text(text = strings.copyPlanToDay)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "کپی کردن تمام حرکات «$sourceDayName» به کدام روز انجام شود؟",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(6.dp)
                ) {
                    for (day in 1..7) {
                        if (day != sourceDayOfWeek) {
                            val targetName = PersianDateHelper.getDayOfWeekName(day, language)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedTargetDay = day }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = targetName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                RadioButton(
                                    selected = selectedTargetDay == day,
                                    onClick = { selectedTargetDay = day },
                                    colors = RadioButtonDefaults.colors(selectedColor = KineticGreen)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedTargetDay) }) {
                Text(text = strings.saveToPlanBtn, color = KineticGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = strings.cancelBtn)
            }
        }
    )
}
