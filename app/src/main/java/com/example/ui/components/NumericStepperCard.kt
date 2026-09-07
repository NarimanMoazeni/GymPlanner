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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.KineticGreen

@Composable
fun NumericStepperCard(
    title: String,
    value: Number,
    onValueChange: (Double) -> Unit,
    step: Double = 1.0,
    minValue: Double = 0.0,
    maxValue: Double = 500.0,
    unitSuffix: String = "",
    dotColor: Color = KineticGreen,
    accentValueColor: Color = MaterialTheme.colorScheme.onSurface,
    quickPresets: List<Pair<String, Double>> = emptyList(),
    presetLabel: String = "",
    testTag: String = "numeric_stepper",
    modifier: Modifier = Modifier
) {
    val language = LocalCurrentLanguage.current
    val strings = LocalAppStrings.current
    var showManualInputDialog by remember { mutableStateOf(false) }

    val isDecimal = value.toDouble() % 1.0 != 0.0 || step % 1.0 != 0.0
    val displayStr = if (isDecimal) {
        String.format("%.1f", value.toDouble())
    } else {
        value.toLong().toString()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Row: Title + Steppers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title with Dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stepper Controls (- Value +)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Minus Button
                IconButton(
                    onClick = {
                        val current = value.toDouble()
                        val newVal = (current - step).coerceIn(minValue, maxValue)
                        onValueChange(newVal)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Value Display Box (Clickable for Manual Input)
                Box(
                    modifier = Modifier
                        .widthIn(min = 68.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .clickable { showManualInputDialog = true }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (unitSuffix.isNotBlank()) {
                            Text(
                                text = unitSuffix,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = formatDigits(displayStr, language),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = accentValueColor
                        )
                    }
                }

                // Plus Button
                IconButton(
                    onClick = {
                        val current = value.toDouble()
                        val newVal = (current + step).coerceIn(minValue, maxValue)
                        onValueChange(newVal)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Quick Presets Row (if provided)
        if (quickPresets.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (presetLabel.isNotBlank()) {
                    Text(
                        text = presetLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                quickPresets.forEach { (chipText, chipVal) ->
                    val isSelected = kotlin.math.abs(value.toDouble() - chipVal) < 0.05
                    val chipBg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                    val chipTextColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(chipBg)
                            .clickable { onValueChange(chipVal) }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatDigits(chipText, language),
                            style = MaterialTheme.typography.labelMedium,
                            color = chipTextColor
                        )
                    }
                }
            }
        }
    }

    // Manual Input Dialog
    if (showManualInputDialog) {
        var inputVal by remember { mutableStateOf(displayStr) }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showManualInputDialog = false },
            title = {
                Text(text = title, style = MaterialTheme.typography.headlineSmall)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = inputVal,
                        onValueChange = {
                            inputVal = it
                            inputError = null
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number),
                        singleLine = true,
                        isError = inputError != null,
                        suffix = { if (unitSuffix.isNotBlank()) Text(unitSuffix) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    inputError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = inputVal.toDoubleOrNull()
                        if (parsed == null || parsed < minValue || parsed > maxValue) {
                            inputError = strings.errInvalidValue
                        } else {
                            onValueChange(parsed)
                            showManualInputDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(strings.saveToPlanBtn)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInputDialog = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }
}
