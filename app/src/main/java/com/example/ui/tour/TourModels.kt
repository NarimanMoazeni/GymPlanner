package com.example.ui.tour

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.AppScreen
import com.example.ui.locale.AppStrings
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SleekIndigo

enum class TourTarget {
    NAV_HOME,
    HOME_TODAY_WORKOUT,
    NAV_PLANNER,
    PLANNER_DAYS_BAR,
    PLANNER_ADD_FAB,
    NAV_REPORT,
    REPORT_WEIGHT_CARD,
    TOUR_FINISH
}

enum class SpotlightShape {
    CIRCLE,
    ROUNDED_RECT
}

data class TourStep(
    val stepIndex: Int,
    val target: TourTarget,
    val screen: AppScreen,
    val shape: SpotlightShape = SpotlightShape.ROUNDED_RECT,
    val title: (AppStrings) -> String,
    val description: (AppStrings) -> String,
    val icon: ImageVector,
    val accentColor: Color
)

val AppTourSteps = listOf(
    // Step 1: Home Tab in Bottom Bar
    TourStep(
        stepIndex = 1,
        target = TourTarget.NAV_HOME,
        screen = AppScreen.HOME,
        shape = SpotlightShape.ROUNDED_RECT,
        title = { it.tourStep1Title },
        description = { it.tourStep1Desc },
        icon = Icons.Default.Home,
        accentColor = EmeraldGreen
    ),
    // Step 2: Today's Workout Program Card on Home Screen
    TourStep(
        stepIndex = 2,
        target = TourTarget.HOME_TODAY_WORKOUT,
        screen = AppScreen.HOME,
        shape = SpotlightShape.ROUNDED_RECT,
        title = { it.tourStep2Title },
        description = { it.tourStep2Desc },
        icon = Icons.Default.FitnessCenter,
        accentColor = EmeraldGreen
    ),
    // Step 3: Weekly 7-Day Bar in Planner Screen
    TourStep(
        stepIndex = 3,
        target = TourTarget.PLANNER_DAYS_BAR,
        screen = AppScreen.PLANNER,
        shape = SpotlightShape.ROUNDED_RECT,
        title = { it.tourStep3Title },
        description = { it.tourStep3Desc },
        icon = Icons.Default.CalendarMonth,
        accentColor = SleekIndigo
    ),
    // Step 4: Floating Add Button (+) in Planner Screen
    TourStep(
        stepIndex = 4,
        target = TourTarget.PLANNER_ADD_FAB,
        screen = AppScreen.PLANNER,
        shape = SpotlightShape.CIRCLE,
        title = { it.tourStep4Title },
        description = { it.tourStep4Desc },
        icon = Icons.Default.Add,
        accentColor = AmberGold
    ),
    // Step 5: Weight Analytics Card in Report Screen
    TourStep(
        stepIndex = 5,
        target = TourTarget.REPORT_WEIGHT_CARD,
        screen = AppScreen.REPORT,
        shape = SpotlightShape.ROUNDED_RECT,
        title = { it.tourStep5Title },
        description = { it.tourStep5Desc },
        icon = Icons.Default.ShowChart,
        accentColor = Color(0xFF38BDF8)
    ),
    // Step 6: Tour Completed
    TourStep(
        stepIndex = 6,
        target = TourTarget.TOUR_FINISH,
        screen = AppScreen.HOME,
        shape = SpotlightShape.ROUNDED_RECT,
        title = { it.tourStep6Title },
        description = { it.tourStep6Desc },
        icon = Icons.Default.CheckCircle,
        accentColor = EmeraldGreen
    )
)

val LocalTourTargetReporter = compositionLocalOf<(TourTarget, Rect) -> Unit> {
    { _, _ -> }
}
