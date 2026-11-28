package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.KineticGreen
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset

@Composable
fun NumericStepperCard(
    title: String,
    value: Number,
    onValueChange: (Double) -> Unit,
    step: Double = 1.0,
    minValue: Double = 0.0,
    maxValue: Double = 500.0,
    unitSuffix: String = "",
    dotColor: Color = EmeraldGreen,
    accentValueColor: Color? = null,
    quickPresets: List<Pair<String, Double>> = emptyList(),
    presetLabel: String = "",
    testTag: String = "numeric_stepper",
    modifier: Modifier = Modifier
) {
    val language = LocalCurrentLanguage.current
    val strings = LocalAppStrings.current
    var showManualInputDialog by rememberSaveable { mutableStateOf(false) }

    val isDecimal = value.toDouble() % 1.0 != 0.0 || step % 1.0 != 0.0
    val displayStr = if (isDecimal) {
        String.format(Locale.US, "%.1f", value.toDouble())
    } else {
        value.toLong().toString()
    }

    val resolvedAccent = accentValueColor ?: MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .tactileCard(shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: Title + 46.dp Physical Steppers and Sunken Display Well
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stepper Controls: (- [Value Display Well] +)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 46.dp Tactile Minus Button
                TactileStepperButton(
                    icon = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    onClick = {
                        val current = value.toDouble()
                        val newVal = (current - step).coerceIn(minValue, maxValue)
                        onValueChange(newVal)
                    }
                )

                // Sunken Digital Readout Well (tactileInset with large bold monospace font)
                Box(
                    modifier = Modifier
                        .widthIn(min = 76.dp)
                        .height(46.dp)
                        .tactileInset(shape = RoundedCornerShape(12.dp))
                        .clickable { showManualInputDialog = true }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatDigits(displayStr, language),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.sp
                            ),
                            maxLines = 1,
                            softWrap = false,
                            color = resolvedAccent
                        )
                        if (unitSuffix.isNotBlank()) {
                            Text(
                                text = unitSuffix,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 46.dp Tactile Plus Button
                TactileStepperButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Increase",
                    onClick = {
                        val current = value.toDouble()
                        val newVal = (current + step).coerceIn(minValue, maxValue)
                        onValueChange(newVal)
                    }
                )
            }
        }

        // Quick Presets Row (Tactile Chips with Elevated Selected State)
        if (quickPresets.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (presetLabel.isNotBlank()) {
                    Text(
                        text = presetLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                quickPresets.forEach { (chipText, chipVal) ->
                    val isSelected = kotlin.math.abs(value.toDouble() - chipVal) < 0.05
                    TactilePresetChip(
                        text = formatDigits(chipText, language),
                        isSelected = isSelected,
                        onClick = { onValueChange(chipVal) }
                    )
                }
            }
        }
    }

    // Manual Input Dialog
    if (showManualInputDialog) {
        var inputVal by rememberSaveable { mutableStateOf(displayStr) }
        var inputError by rememberSaveable { mutableStateOf<String?>(null) }

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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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

/**
 * 46.dp Physical Tactile Stepper Button (+ / -) with 3D Lip and Spring Press
 */
@Composable
private fun TactileStepperButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "stepper_press"
    )
    val bottomLipHeight by animateDpAsState(
        targetValue = if (isPressed) 0.5.dp else 2.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "stepper_lip"
    )

    val faceColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface
    val lipColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerLowest else Color(0xFFCBD5E1)
    val borderColor = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(lipColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(bottom = bottomLipHeight)
    ) {
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .size(width = 46.dp, height = (46 - 2.5).dp)
                .clip(RoundedCornerShape(13.dp))
                .background(faceColor)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(13.dp))
                .drawBehind {
                    val hl = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.60f)
                    drawLine(
                        color = hl,
                        start = Offset(x = 6f, y = 1.5f),
                        end = Offset(x = size.width - 6f, y = 1.5f),
                        strokeWidth = 2f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Tactile Quick Preset Chip with elevated selected state using primary / emerald
 */
@Composable
private fun TactilePresetChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "chip_press"
    )

    if (isSelected) {
        val selectedBg = if (isDark) EmeraldGreen else ElectricIndigo
        val lipColor = if (isDark) Color(0xFF064E3B) else Color(0xFF312E81)
        Box(
            modifier = modifier
                .height(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(lipColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(bottom = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(y = pressOffsetY)
                    .clip(RoundedCornerShape(9.dp))
                    .background(selectedBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .height(32.dp)
                .tactileInset(shape = RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
