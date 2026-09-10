package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppPreferences
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.CompletedActivityEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.local.entity.WorkoutSessionEntity
import com.example.data.model.ActivityType
import com.example.data.model.WeightUnit
import com.example.data.repository.FitPlanRepository
import com.example.ui.locale.AppLanguage
import com.example.ui.theme.ThemeMode
import com.example.util.PersianDateHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppScreen {
    ONBOARDING,
    HOME,
    PLANNER,
    REPORT,
    ACTIVE_WORKOUT,
    SETTINGS
}

data class FitPlanUiState(
    val currentScreen: AppScreen = AppScreen.ONBOARDING,
    val profile: UserProfileEntity = UserProfileEntity(),
    val workoutDays: List<WorkoutDayEntity> = emptyList(),
    val activitiesByDay: Map<Int, List<ActivityEntity>> = emptyMap(),
    val selectedPlannerDay: Int = 2, // 1=Sat..7=Fri
    val todayWeekIndex: Int = 4, // default Tuesday (سه‌شنبه)
    val todayDateKey: String = "",
    val todaySession: WorkoutSessionEntity? = null,
    val completedActivitiesToday: List<CompletedActivityEntity> = emptyList(),
    val completedActivitiesPlannerDay: List<CompletedActivityEntity> = emptyList(),
    val allSessions: List<WorkoutSessionEntity> = emptyList(),
    val allCompletedActivities: List<CompletedActivityEntity> = emptyList(),
    val preferences: AppPreferences = AppPreferences(),
    val isAddEditSheetOpen: Boolean = false,
    val editingActivity: ActivityEntity? = null,
    val addEditTargetDay: Int = 2,
    val deleteConfirmActivity: ActivityEntity? = null,
    val copyPlanSourceDay: Int? = null,
    val feedbackMessage: String? = null,
    val isLoading: Boolean = false
)

class FitPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FitPlanRepository.getInstance(application)

    private val _uiState = MutableStateFlow(FitPlanUiState())
    val uiState: StateFlow<FitPlanUiState> = _uiState.asStateFlow()
    private var plannerCompletionsJob: Job? = null

    init {
        val cal = Calendar.getInstance()
        val todayWeek = PersianDateHelper.calendarDayToWeekIndex(cal.get(Calendar.DAY_OF_WEEK))
        val todayKey = PersianDateHelper.getTodayDateKey()

        _uiState.update {
            it.copy(
                todayWeekIndex = todayWeek,
                selectedPlannerDay = todayWeek,
                todayDateKey = todayKey
            )
        }

        viewModelScope.launch {
            val launchPrefs = application.getSharedPreferences("fitplan_launch_prefs", Context.MODE_PRIVATE)
            val isFreshInstallResetDone = launchPrefs.getBoolean("fresh_install_v3_ready", false)
            if (!isFreshInstallResetDone) {
                repository.resetToFreshInstall()
                launchPrefs.edit().putBoolean("fresh_install_v3_ready", true).apply()
            } else {
                repository.preSeedDefaultDataIfEmpty()
            }
        }

        // Combine flows to update UI state reactively
        viewModelScope.launch {
            combine(
                repository.userProfileFlow,
                repository.workoutDaysFlow,
                repository.preferencesFlow
            ) { profile, days, prefs ->
                Triple(profile, days, prefs)
            }.collect { (profile, days, prefs) ->
                _uiState.update { current ->
                    val user = profile ?: UserProfileEntity()
                    val targetScreen = if (!user.hasCompletedOnboarding) {
                        AppScreen.ONBOARDING
                    } else if (current.currentScreen == AppScreen.ONBOARDING) {
                        AppScreen.HOME
                    } else {
                        current.currentScreen
                    }

                    current.copy(
                        profile = user,
                        workoutDays = days,
                        preferences = prefs,
                        currentScreen = targetScreen
                    )
                }
            }
        }

        // Observe activities for all 7 days
        for (day in 1..7) {
            viewModelScope.launch {
                repository.getActivitiesForDay(day).collect { acts ->
                    _uiState.update { current ->
                        val map = current.activitiesByDay.toMutableMap()
                        map[day] = acts
                        current.copy(activitiesByDay = map)
                    }
                }
            }
        }

        // Observe today's session and completion
        viewModelScope.launch {
            repository.getSessionForDate(todayKey).collect { session ->
                _uiState.update { it.copy(todaySession = session) }
            }
        }

        viewModelScope.launch {
            repository.getCompletedActivitiesForDate(todayKey).collect { completed ->
                _uiState.update { it.copy(completedActivitiesToday = completed) }
            }
        }

        viewModelScope.launch {
            repository.allSessionsFlow.collect { sessions ->
                _uiState.update { it.copy(allSessions = sessions) }
            }
        }

        viewModelScope.launch {
            repository.allCompletedActivitiesFlow.collect { acts ->
                _uiState.update { it.copy(allCompletedActivities = acts) }
            }
        }

        observePlannerDayCompletions(todayWeek)
    }

    private fun observePlannerDayCompletions(dayOfWeek: Int) {
        plannerCompletionsJob?.cancel()
        plannerCompletionsJob = viewModelScope.launch {
            val dateKey = PersianDateHelper.getDateKeyForDayOfWeek(dayOfWeek)
            repository.getCompletedActivitiesForDate(dateKey).collect { completed ->
                _uiState.update { it.copy(completedActivitiesPlannerDay = completed) }
            }
        }
    }

    fun completeOnboarding(name: String, age: Int?) {
        viewModelScope.launch {
            repository.saveUserProfile(name, age, hasCompletedOnboarding = true)
            _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            repository.saveUserProfile("ورزشکار", 26, hasCompletedOnboarding = true)
            _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
        }
    }

    fun resetAppToFreshInstall() {
        viewModelScope.launch {
            repository.resetToFreshInstall()
            _uiState.update {
                it.copy(
                    currentScreen = AppScreen.ONBOARDING,
                    profile = UserProfileEntity(id = 1, name = "", age = null, hasCompletedOnboarding = false),
                    workoutDays = (1..7).map { dayIndex ->
                        WorkoutDayEntity(
                            dayOfWeek = dayIndex,
                            isTrainingDay = false,
                            focusName = "",
                            focusSubtitle = "",
                            estimatedDurationMinutes = 45
                        )
                    },
                    activitiesByDay = emptyMap(),
                    completedActivitiesToday = emptyList(),
                    allSessions = emptyList(),
                    feedbackMessage = "برنامه به حالت تازه نصب شده بازنشانی شد."
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectPlannerDay(dayOfWeek: Int) {
        _uiState.update { it.copy(selectedPlannerDay = dayOfWeek) }
        observePlannerDayCompletions(dayOfWeek)
    }

    fun updateDayFocus(dayOfWeek: Int, focusTitle: String) {
        viewModelScope.launch {
            repository.updateDayFocus(dayOfWeek, focusTitle.trim())
        }
    }

    fun setWeeklyGoal(goal: String) {
        viewModelScope.launch {
            repository.setWeeklyGoal(goal.trim())
        }
    }

    fun logWeight(weight: Float) {
        viewModelScope.launch {
            val dateKey = PersianDateHelper.getTodayDateKey()
            repository.logWeightEntry(weight, dateKey)
        }
    }

    fun updateUserHeight(height: Float) {
        viewModelScope.launch {
            repository.setUserHeight(height)
        }
    }

    fun updateUserWeight(weight: Float) {
        viewModelScope.launch {
            repository.setUserWeight(weight)
        }
    }

    fun togglePlannerActivityDone(activity: ActivityEntity) {
        viewModelScope.launch {
            val dayOfWeek = _uiState.value.selectedPlannerDay
            val dateKey = PersianDateHelper.getDateKeyForDayOfWeek(dayOfWeek)
            val isDone = _uiState.value.completedActivitiesPlannerDay.any { it.activityId == activity.id }
            repository.toggleActivityCompletionForDate(
                activity = activity,
                dayOfWeek = dayOfWeek,
                dateKey = dateKey,
                isCompleted = !isDone
            )
        }
    }

    fun toggleTrainingDay(dayOfWeek: Int) {
        viewModelScope.launch {
            val day = _uiState.value.workoutDays.find { it.dayOfWeek == dayOfWeek }
            val currentStatus = day?.isTrainingDay ?: false
            repository.toggleTrainingDay(dayOfWeek, !currentStatus)
        }
    }

    fun openAddActivity(dayOfWeek: Int) {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = true,
                editingActivity = null,
                addEditTargetDay = dayOfWeek
            )
        }
    }

    fun openEditActivity(activity: ActivityEntity) {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = true,
                editingActivity = activity,
                addEditTargetDay = activity.dayOfWeek
            )
        }
    }

    fun closeAddEditSheet() {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = false,
                editingActivity = null
            )
        }
    }

    fun saveActivity(
        name: String,
        subtitle: String,
        type: ActivityType,
        sets: Int,
        reps: Int,
        weight: Double,
        duration: Int,
        distance: Double
    ) {
        viewModelScope.launch {
            val currentEditing = _uiState.value.editingActivity
            if (currentEditing != null) {
                repository.updateActivity(
                    currentEditing.copy(
                        name = name.trim(),
                        subtitle = subtitle.trim(),
                        type = type,
                        sets = sets,
                        reps = reps,
                        weight = weight,
                        durationMinutes = duration,
                        distanceKm = distance
                    )
                )
            } else {
                repository.addActivity(
                    ActivityEntity(
                        dayOfWeek = _uiState.value.addEditTargetDay,
                        name = name.trim(),
                        subtitle = subtitle.trim(),
                        type = type,
                        sets = sets,
                        reps = reps,
                        weight = weight,
                        durationMinutes = duration,
                        distanceKm = distance
                    )
                )
            }
            closeAddEditSheet()
            showFeedback("ذخیره در برنامه انجام شد")
        }
    }

    fun promptDeleteActivity(activity: ActivityEntity) {
        _uiState.update { it.copy(deleteConfirmActivity = activity) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(deleteConfirmActivity = null) }
    }

    fun confirmDeleteActivity() {
        val target = _uiState.value.deleteConfirmActivity ?: return
        viewModelScope.launch {
            repository.deleteActivity(target.id)
            dismissDeleteDialog()
            showFeedback("فعالیت حذف شد")
        }
    }

    fun duplicateActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.duplicateActivity(activity.id)
            showFeedback("فعالیت کپی شد")
        }
    }

    fun moveActivityUp(dayOfWeek: Int, activity: ActivityEntity) {
        val currentList = _uiState.value.activitiesByDay[dayOfWeek] ?: return
        val idx = currentList.indexOfFirst { it.id == activity.id }
        if (idx > 0) {
            val mutable = currentList.toMutableList()
            val temp = mutable[idx]
            mutable[idx] = mutable[idx - 1]
            mutable[idx - 1] = temp
            viewModelScope.launch {
                repository.reorderActivities(dayOfWeek, mutable)
            }
        }
    }

    fun moveActivityDown(dayOfWeek: Int, activity: ActivityEntity) {
        val currentList = _uiState.value.activitiesByDay[dayOfWeek] ?: return
        val idx = currentList.indexOfFirst { it.id == activity.id }
        if (idx in 0 until currentList.size - 1) {
            val mutable = currentList.toMutableList()
            val temp = mutable[idx]
            mutable[idx] = mutable[idx + 1]
            mutable[idx + 1] = temp
            viewModelScope.launch {
                repository.reorderActivities(dayOfWeek, mutable)
            }
        }
    }

    fun openCopyPlanDialog(sourceDay: Int) {
        _uiState.update { it.copy(copyPlanSourceDay = sourceDay) }
    }

    fun dismissCopyPlanDialog() {
        _uiState.update { it.copy(copyPlanSourceDay = null) }
    }

    fun confirmCopyPlan(targetDay: Int) {
        val src = _uiState.value.copyPlanSourceDay ?: return
        viewModelScope.launch {
            repository.copyDayPlan(src, targetDay)
            dismissCopyPlanDialog()
            showFeedback("برنامه روز با موفقیت کپی شد")
        }
    }

    fun toggleActivityDone(activity: ActivityEntity) {
        viewModelScope.launch {
            val isDone = _uiState.value.completedActivitiesToday.any { it.activityId == activity.id }
            repository.toggleActivityCompletion(activity, !isDone)
        }
    }

    fun finishActiveSession() {
        viewModelScope.launch {
            repository.finishTodayWorkout()
            showFeedback("جلسه تمرینی با موفقیت ثبت شد")
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(themeMode)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            repository.setLanguage(language)
        }
    }

    fun setWeightUnit(unit: WeightUnit) {
        viewModelScope.launch {
            repository.setWeightUnit(unit)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setReminderEnabled(enabled)
        }
    }

    fun updateUserProfile(name: String, age: Int?) {
        viewModelScope.launch {
            repository.saveUserProfile(name, age, hasCompletedOnboarding = true)
            showFeedback("پروفایل به‌روزرسانی شد")
        }
    }

    fun setUserHeight(height: Float) {
        viewModelScope.launch {
            repository.setUserHeight(height)
        }
    }

    fun setUserWeight(weight: Float) {
        viewModelScope.launch {
            repository.setUserWeight(weight)
        }
    }

    fun logWeightEntry(weight: Float, dateKey: String = PersianDateHelper.getTodayDateKey()) {
        viewModelScope.launch {
            repository.logWeightEntry(weight, dateKey)
        }
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportBackupJson()
            onResult(json)
            showFeedback("فایل پشتیبان ایجاد شد")
        }
    }

    fun importBackup(jsonString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val res = repository.importBackupJson(jsonString)
            if (res.isSuccess) {
                showFeedback("داده‌ها با موفقیت بازیابی شدند")
                onResult(true)
            } else {
                showFeedback("خطا در بازیابی فایل پشتیبان")
                onResult(false)
            }
        }
    }

    private fun showFeedback(msg: String) {
        _uiState.update { it.copy(feedbackMessage = msg) }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
