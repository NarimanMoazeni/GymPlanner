package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddEditActivitySheet
import com.example.ui.components.CopyDayPlanDialog
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.FitPlanBottomNav
import com.example.ui.locale.FitPlanLocalizationProvider
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.FitPlanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitPlanApp(
    viewModel: FitPlanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isColdStartSplashDone by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    // Handle back button behavior (only active when splash is done)
    BackHandler(enabled = isColdStartSplashDone && uiState.currentScreen != AppScreen.HOME && uiState.currentScreen != AppScreen.ONBOARDING) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    FitPlanTheme(themeMode = uiState.preferences.themeMode) {
        FitPlanLocalizationProvider(language = uiState.preferences.language) {
            AnimatedContent(
                targetState = isColdStartSplashDone,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "splash_to_main_transition"
            ) { splashDone ->
                if (!splashDone) {
                    SplashScreen(
                        onSplashFinished = {
                            isColdStartSplashDone = true
                        }
                    )
                } else {
                    val showBottomBar = uiState.currentScreen in listOf(
                        AppScreen.HOME,
                        AppScreen.PLANNER,
                        AppScreen.REPORT,
                        AppScreen.SETTINGS
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            contentWindowInsets = WindowInsets.statusBars,
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                if (showBottomBar) {
                                    FitPlanBottomNav(
                                        currentScreen = uiState.currentScreen,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                            }
                        ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = uiState.currentScreen,
                        transitionSpec = {
                            val tabOrder = mapOf(
                                AppScreen.HOME to 0,
                                AppScreen.PLANNER to 1,
                                AppScreen.REPORT to 2,
                                AppScreen.SETTINGS to 3
                            )
                            val initialOrder = tabOrder[initialState]
                            val targetOrder = tabOrder[targetState]

                            if (initialOrder != null && targetOrder != null) {
                                val direction = if (targetOrder > initialOrder) 1 else -1
                                (slideInHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
                                    initialOffsetX = { fullWidth -> direction * fullWidth / 4 }
                                ) + fadeIn(
                                    animationSpec = tween(durationMillis = 250)
                                )).togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
                                        targetOffsetX = { fullWidth -> -direction * fullWidth / 4 }
                                    ) + fadeOut(
                                        animationSpec = tween(durationMillis = 200)
                                    )
                                )
                            } else if (targetState == AppScreen.ACTIVE_WORKOUT) {
                                (slideInVertically(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                    initialOffsetY = { fullHeight -> fullHeight / 3 }
                                ) + fadeIn(tween(250))).togetherWith(fadeOut(tween(200)))
                            } else if (initialState == AppScreen.ACTIVE_WORKOUT) {
                                fadeIn(tween(250)).togetherWith(
                                    slideOutVertically(
                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                        targetOffsetY = { fullHeight -> fullHeight / 3 }
                                    ) + fadeOut(tween(200))
                                )
                            } else {
                                fadeIn(tween(250)).togetherWith(fadeOut(tween(200)))
                            }
                        },
                        label = "screen_transition"
                    ) { currentTargetScreen ->
                        when (currentTargetScreen) {
                            AppScreen.ONBOARDING -> {
                                OnboardingScreen(
                                    onComplete = { name, age ->
                                        viewModel.completeOnboarding(name, age)
                                    },
                                    onSkip = {
                                        viewModel.skipOnboarding()
                                    }
                                )
                            }

                            AppScreen.HOME -> {
                                val completedIds = uiState.completedActivitiesToday.map { it.activityId }.toSet()
                                HomeScreen(
                                    profile = uiState.profile,
                                    workoutDays = uiState.workoutDays,
                                    activitiesMap = uiState.activitiesByDay,
                                    todayWeekIndex = uiState.todayWeekIndex,
                                    todaySession = uiState.todaySession,
                                    completedActivityIds = completedIds,
                                    weeklyGoal = uiState.preferences.weeklyGoal,
                                    weightUnit = uiState.preferences.weightUnit,
                                    onUpdateWeeklyGoal = { goal -> viewModel.setWeeklyGoal(goal) },
                                    onToggleActivityDone = { act ->
                                        viewModel.toggleActivityDone(act)
                                    },
                                    onStartWorkout = {
                                        viewModel.navigateTo(AppScreen.ACTIVE_WORKOUT)
                                    },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onSelectDayInPlanner = { day ->
                                        viewModel.selectPlannerDay(day)
                                    }
                                )
                            }

                            AppScreen.PLANNER -> {
                                PlannerScreen(
                                    selectedDayIndex = uiState.selectedPlannerDay,
                                    todayWeekIndex = uiState.todayWeekIndex,
                                    workoutDays = uiState.workoutDays,
                                    activitiesMap = uiState.activitiesByDay,
                                    weightUnit = uiState.preferences.weightUnit,
                                    completedActivityIds = uiState.completedActivitiesPlannerDay.map { it.activityId }.toSet(),
                                    onToggleActivityDone = { act -> viewModel.togglePlannerActivityDone(act) },
                                    onUpdateDayFocus = { day, focus -> viewModel.updateDayFocus(day, focus) },
                                    onSelectDay = { viewModel.selectPlannerDay(it) },
                                    onToggleTrainingDay = { viewModel.toggleTrainingDay(it) },
                                    onAddActivity = { day -> viewModel.openAddActivity(day) },
                                    onEditActivity = { act -> viewModel.openEditActivity(act) },
                                    onDuplicateActivity = { act -> viewModel.duplicateActivity(act) },
                                    onDeleteActivity = { act -> viewModel.promptDeleteActivity(act) },
                                    onMoveUp = { day, act -> viewModel.moveActivityUp(day, act) },
                                    onMoveDown = { day, act -> viewModel.moveActivityDown(day, act) },
                                    onCopyPlan = { srcDay -> viewModel.openCopyPlanDialog(srcDay) }
                                )
                            }

                            AppScreen.REPORT -> {
                                ReportScreen(
                                    userWeightKg = uiState.preferences.userWeightKg,
                                    userHeightCm = uiState.preferences.userHeightCm,
                                    weightHistoryRaw = uiState.preferences.weightHistoryRaw,
                                    weightUnit = uiState.preferences.weightUnit,
                                    allSessions = uiState.allSessions,
                                    allCompletedActivities = uiState.allCompletedActivities,
                                    workoutDays = uiState.workoutDays,
                                    activitiesMap = uiState.activitiesByDay,
                                    todayWeekIndex = uiState.todayWeekIndex,
                                    onLogWeight = { w -> viewModel.logWeight(w) },
                                    onUpdateHeight = { h -> viewModel.updateUserHeight(h) },
                                    onNavigateToPlannerDay = { day ->
                                        viewModel.selectPlannerDay(day)
                                        viewModel.navigateTo(AppScreen.PLANNER)
                                    }
                                )
                            }

                            AppScreen.ACTIVE_WORKOUT -> {
                                val todayDayEntity = uiState.workoutDays.find { it.dayOfWeek == uiState.todayWeekIndex }
                                val todayActivities = uiState.activitiesByDay[uiState.todayWeekIndex] ?: emptyList()

                                ActiveWorkoutScreen(
                                    todayWeekIndex = uiState.todayWeekIndex,
                                    workoutDay = todayDayEntity,
                                    activities = todayActivities,
                                    completedActivities = uiState.completedActivitiesToday,
                                    weightUnit = uiState.preferences.weightUnit,
                                    onToggleDone = { act -> viewModel.toggleActivityDone(act) },
                                    onFinishWorkout = {
                                        viewModel.finishActiveSession()
                                        viewModel.navigateTo(AppScreen.HOME)
                                    },
                                    onClose = {
                                        viewModel.navigateTo(AppScreen.HOME)
                                    }
                                )
                            }

                            AppScreen.SETTINGS -> {
                                SettingsScreen(
                                    profile = uiState.profile,
                                    preferences = uiState.preferences,
                                    onUpdateProfile = { name, age ->
                                        viewModel.updateUserProfile(name, age)
                                    },
                                    onSetThemeMode = { viewModel.setThemeMode(it) },
                                    onSetLanguage = { viewModel.setLanguage(it) },
                                    onSetWeightUnit = { viewModel.setWeightUnit(it) },
                                    onSetReminder = { viewModel.setReminderEnabled(it) },
                                    onExportBackup = { callback ->
                                        viewModel.exportBackup(callback)
                                    },
                                    onImportBackup = { json, callback ->
                                        viewModel.importBackup(json, callback)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Add / Edit Modal Bottom Sheet
            if (uiState.isAddEditSheetOpen) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                AddEditActivitySheet(
                    sheetState = sheetState,
                    initialActivity = uiState.editingActivity,
                    targetDayOfWeek = uiState.addEditTargetDay,
                    weightUnit = uiState.preferences.weightUnit,
                    onDismiss = { viewModel.closeAddEditSheet() },
                    onSave = { name, subtitle, type, sets, reps, weight, duration, distance ->
                        viewModel.saveActivity(name, subtitle, type, sets, reps, weight, duration, distance)
                    }
                )
            }

            // Delete Confirmation Dialog
            uiState.deleteConfirmActivity?.let { act ->
                DeleteConfirmDialog(
                    activity = act,
                    onDismiss = { viewModel.dismissDeleteDialog() },
                    onConfirm = { viewModel.confirmDeleteActivity() }
                )
            }

            // Copy Plan Dialog
            uiState.copyPlanSourceDay?.let { srcDay ->
                CopyDayPlanDialog(
                    sourceDayOfWeek = srcDay,
                    onDismiss = { viewModel.dismissCopyPlanDialog() },
                    onConfirm = { targetDay -> viewModel.confirmCopyPlan(targetDay) }
                )
            }
                    }
                }
            }
        }
    }
}
