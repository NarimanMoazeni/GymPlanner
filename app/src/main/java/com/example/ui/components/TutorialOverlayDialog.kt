package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.LocalCurrentLanguage
import com.example.ui.locale.formatDigits
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SleekIndigo

private data class TutorialStepData(
    val stepIndex: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val highlights: List<String>,
    val badgeText: String
)

@Composable
fun TutorialOverlayDialog(
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalCurrentLanguage.current
    var currentStep by remember { mutableIntStateOf(0) }
    var previousStep by remember { mutableIntStateOf(0) }

    val steps = remember(strings, language) {
        listOf(
            TutorialStepData(
                stepIndex = 1,
                title = strings.tutorialStep1Title,
                description = strings.tutorialStep1Desc,
                icon = Icons.Default.Dashboard,
                accentColor = EmeraldGreen,
                badgeText = if (language == AppLanguage.PERSIAN) "داشبورد هوشمند" else "Smart Dashboard",
                highlights = if (language == AppLanguage.PERSIAN) listOf(
                    "بررسی وضعیت تمرین یا استراحت امروز",
                    "خلاصه کل حرکات، ست‌ها و تایم تخمینی",
                    "تنظیم هدف هفتگی ورزشی اختصاصی"
                ) else listOf(
                    "Inspect today's workout or recovery status",
                    "Overview of exercises, total sets & time",
                    "Set your personalized weekly fitness goal"
                )
            ),
            TutorialStepData(
                stepIndex = 2,
                title = strings.tutorialStep2Title,
                description = strings.tutorialStep2Desc,
                icon = Icons.Default.CalendarMonth,
                accentColor = SleekIndigo,
                badgeText = if (language == AppLanguage.PERSIAN) "تقویم و برنامه هفتگی" else "Weekly Schedule",
                highlights = if (language == AppLanguage.PERSIAN) listOf(
                    "تفکیک روزهای تمرین از شنبه تا جمعه",
                    "تعیین عنوان و تمرکز عضله برای هر روز",
                    "تقویم پیشرفته ماهانه با نمایش جلسات"
                ) else listOf(
                    "7-day split from Saturday to Friday",
                    "Custom focus & muscle targets per day",
                    "Interactive monthly overview calendar"
                )
            ),
            TutorialStepData(
                stepIndex = 3,
                title = strings.tutorialStep3Title,
                description = strings.tutorialStep3Desc,
                icon = Icons.Default.FitnessCenter,
                accentColor = AmberGold,
                badgeText = if (language == AppLanguage.PERSIAN) "مدیریت حرکات" else "Exercise Library",
                highlights = if (language == AppLanguage.PERSIAN) listOf(
                    "دکمه شناور (+) برای افزودن هر حرکت دلخواه",
                    "پشتیبانی از وزنه، ست، تکرار، زمان و مسافت",
                    "کپی سریع کل حرکات یک روز به روزهای دیگر"
                ) else listOf(
                    "Floating (+) button to add any custom movement",
                    "Full support for weights, reps, time & distance",
                    "Quick 1-tap clone between different days"
                )
            ),
            TutorialStepData(
                stepIndex = 4,
                title = strings.tutorialStep4Title,
                description = strings.tutorialStep4Desc,
                icon = Icons.Default.PlayArrow,
                accentColor = Color(0xFF10B981),
                badgeText = if (language == AppLanguage.PERSIAN) "جلسه تمرین زنده" else "Live Workout",
                highlights = if (language == AppLanguage.PERSIAN) listOf(
                    "تیک زدن پیشرفت حرکات ست به ست حین تمرین",
                    "تایمر خودکار و هوشمند استراحت بین ست‌ها",
                    "محاسبه مجموع تناژ و ثبت رسمی جلسه"
                ) else listOf(
                    "Check off sets step-by-step during exercise",
                    "Smart automatic rest stopwatch between sets",
                    "Automatic tonnage tally & session log"
                )
            ),
            TutorialStepData(
                stepIndex = 5,
                title = strings.tutorialStep5Title,
                description = strings.tutorialStep5Desc,
                icon = Icons.Default.ShowChart,
                accentColor = Color(0xFF38BDF8),
                badgeText = if (language == AppLanguage.PERSIAN) "آنالیز و گزارش" else "Progress & BMI",
                highlights = if (language == AppLanguage.PERSIAN) listOf(
                    "نمودار روند تغییرات وزن و وزن هدف",
                    "محاسبه هوشمند شاخص توده بدنی (BMI)",
                    "ردیابی تسلسل روزهای تمرین (Streak 🔥)"
                ) else listOf(
                    "Visual weight progress trend curve",
                    "Smart Body Mass Index (BMI) calculator",
                    "Consistency streaks and workout history 🔥"
                )
            )
        )
    }

    val totalSteps = steps.size
    val currentStepData = steps.getOrElse(currentStep) { steps.first() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .padding(horizontal = 20.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF121620),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    currentStepData.accentColor.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar: Step Counter Pill + Skip Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Step Counter Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E2536))
                                .border(1.dp, Color(0xFF2D3748), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            val counterText = strings.tutorialStepCounter.format(
                                formatDigits(currentStep + 1, language),
                                formatDigits(totalSteps, language)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = currentStepData.accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = counterText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }

                        // Skip Button
                        TextButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = strings.tutorialSkipBtn,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Animated Step Content
                    val isForward = currentStep >= previousStep
                    AnimatedContent(
                        targetState = currentStepData,
                        transitionSpec = {
                            if (isForward) {
                                (slideInHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                    initialOffsetX = { fullWidth -> fullWidth / 2 }
                                ) + fadeIn(tween(250))) togetherWith (slideOutHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                    targetOffsetX = { fullWidth -> -fullWidth / 2 }
                                ) + fadeOut(tween(200)))
                            } else {
                                (slideInHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                    initialOffsetX = { fullWidth -> -fullWidth / 2 }
                                ) + fadeIn(tween(250))) togetherWith (slideOutHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                    targetOffsetX = { fullWidth -> fullWidth / 2 }
                                ) + fadeOut(tween(200)))
                            }
                        },
                        label = "tutorial_step_transition"
                    ) { step ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Glowing Icon Card
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(
                                                step.accentColor.copy(alpha = 0.85f),
                                                Color.White.copy(alpha = 0.4f),
                                                step.accentColor.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                                    .padding(2.5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color(0xFF161B26)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = step.icon,
                                        contentDescription = null,
                                        tint = step.accentColor,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Category Tag Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(step.accentColor.copy(alpha = 0.14f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = step.badgeText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    ),
                                    color = step.accentColor
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Title
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Description
                            Text(
                                text = step.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.5.sp,
                                    lineHeight = 19.sp
                                ),
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Highlights Box
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF161C27))
                                    .border(1.dp, Color(0xFF263242), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                step.highlights.forEach { highlight ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(step.accentColor.copy(alpha = 0.18f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = step.accentColor,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                        Text(
                                            text = highlight,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 11.5.sp
                                            ),
                                            color = Color(0xFFCBD5E1)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Progress Capsule Indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until totalSteps) {
                            val isActive = i == currentStep
                            val animatedWidth by animateDpAsState(
                                targetValue = if (isActive) 24.dp else 8.dp,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "dot_width"
                            )
                            val dotColor = if (isActive) currentStepData.accentColor else Color(0xFF334155)

                            Box(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(animatedWidth)
                                    .clip(CircleShape)
                                    .background(dotColor)
                                    .clickable {
                                        previousStep = currentStep
                                        currentStep = i
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Navigation Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Button (only shown if currentStep > 0)
                        if (currentStep > 0) {
                            OutlinedButton(
                                onClick = {
                                    if (currentStep > 0) {
                                        previousStep = currentStep
                                        currentStep--
                                    }
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFCBD5E1)
                                )
                            ) {
                                Text(
                                    text = strings.tutorialPrevBtn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Next / Get Started Button
                        val isLastStep = currentStep == totalSteps - 1
                        Button(
                            onClick = {
                                if (isLastStep) {
                                    onComplete()
                                } else {
                                    previousStep = currentStep
                                    currentStep++
                                }
                            },
                            modifier = Modifier
                                .weight(if (currentStep > 0) 1.3f else 1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentStepData.accentColor,
                                contentColor = if (currentStepData.accentColor == AmberGold) Color.Black else Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isLastStep) strings.tutorialStartBtn else strings.tutorialNextBtn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Icon(
                                    imageVector = if (isLastStep) Icons.Default.Check else {
                                        if (language == AppLanguage.PERSIAN) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
