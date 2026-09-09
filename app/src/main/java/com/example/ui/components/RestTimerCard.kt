package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkAmberGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.tactileButton
import com.example.ui.theme.tactileCard
import com.example.ui.theme.tactileInset
import kotlinx.coroutines.delay

/**
 * Rugged Gym Stopwatch Style Rest Timer Card:
 * - Monospace countdown inside a debossed dark/light well (tactileInset)
 * - Pulsating soft glow when the timer is actively counting down
 * - Raised physical tactile +30s button in Amber Accent
 * - Tactile neutral squircle reset button
 */
@Composable
fun RestTimerCard(
    modifier: Modifier = Modifier,
    initialSeconds: Int = 42
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    var secondsLeft by remember { mutableIntStateOf(initialSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
    }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    // Pulsating soft glow animation when running
    val infiniteTransition = rememberInfiniteTransition(label = "timer_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val accentAmber = if (isDark) DarkAmberGold else AmberGold
    val borderGlowColor = if (isRunning && secondsLeft > 0) {
        accentAmber.copy(alpha = pulseAlpha)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .tactileCard(
                shape = RoundedCornerShape(20.dp),
                borderColor = borderGlowColor,
                elevation = if (isRunning && secondsLeft > 0) 8.dp else 4.dp
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("rest_timer_card"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left info: Stopwatch Icon + Title + Monospace Countdown inside Debossed Well
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stopwatch Icon Squircle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentAmber.copy(alpha = 0.15f))
                    .border(1.dp, accentAmber.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = strings.restTimerTitle,
                    tint = accentAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.restTimerTitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Sunken Digital Well for Countdown
                Box(
                    modifier = Modifier
                        .tactileInset(shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatDigits(timeFormatted, language),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 22.sp
                        ),
                        color = if (secondsLeft > 0) accentAmber else EmeraldGreen
                    )
                }
            }
        }

        // Right controls: Raised Tactile +30s Button and Tactile Reset Squircle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // +30s Button (Raised Amber Tactile Button)
            val amberLip = if (isDark) Color(0xFF78350F) else Color(0xFFB45309)
            Box(
                modifier = Modifier
                    .height(42.dp)
                    .tactileButton(
                        onClick = {
                            secondsLeft += 30
                            isRunning = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        baseColor = accentAmber,
                        lipColor = amberLip,
                        borderColor = accentAmber.copy(alpha = 0.5f)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.add30SecBtn,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    ),
                    color = Color.White
                )
            }

            // Reset Button (Tactile Neutral Squircle)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .tactileButton(
                        onClick = {
                            secondsLeft = 60
                            isRunning = true
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = strings.resetTimerBtn,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
