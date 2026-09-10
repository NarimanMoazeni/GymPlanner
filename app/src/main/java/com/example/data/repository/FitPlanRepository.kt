package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.local.PreferencesManager
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.CompletedActivityEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.local.entity.WorkoutSessionEntity
import com.example.data.model.ActivityType
import com.example.data.model.WeightUnit
import com.example.ui.locale.AppLanguage
import com.example.ui.theme.ThemeMode
import com.example.util.PersianDateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class FitPlanRepository(
    private val database: AppDatabase,
    private val preferencesManager: PreferencesManager
) {
    private val dao = database.fitPlanDao()

    val userProfileFlow: Flow<UserProfileEntity?> = dao.getUserProfile()
    val workoutDaysFlow: Flow<List<WorkoutDayEntity>> = dao.getAllWorkoutDays()
    val allSessionsFlow: Flow<List<WorkoutSessionEntity>> = dao.getAllSessions()
    val allCompletedActivitiesFlow: Flow<List<CompletedActivityEntity>> = dao.getAllCompletedActivities()
    val preferencesFlow: Flow<AppPreferences> = preferencesManager.preferencesFlow

    fun getActivitiesForDay(dayOfWeek: Int): Flow<List<ActivityEntity>> {
        return dao.getActivitiesForDay(dayOfWeek)
    }

    fun getCompletedActivitiesForDate(dateString: String): Flow<List<CompletedActivityEntity>> {
        return dao.getCompletedActivitiesForDate(dateString)
    }

    fun getSessionForDate(dateString: String): Flow<WorkoutSessionEntity?> {
        return dao.getSessionForDate(dateString)
    }

    suspend fun saveUserProfile(name: String, age: Int?, hasCompletedOnboarding: Boolean) {
        withContext(Dispatchers.IO) {
            dao.upsertUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = name.ifBlank { "نیما" },
                    age = age,
                    hasCompletedOnboarding = hasCompletedOnboarding
                )
            )
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        preferencesManager.setLanguage(language)
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        preferencesManager.setThemeMode(themeMode)
    }

    suspend fun setWeightUnit(unit: WeightUnit) {
        preferencesManager.setWeightUnit(unit)
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        preferencesManager.setReminderEnabled(enabled)
    }

    suspend fun setWeeklyGoal(goal: String) {
        preferencesManager.setWeeklyGoal(goal)
    }

    suspend fun setUserWeight(weight: Float) {
        preferencesManager.setUserWeight(weight)
    }

    suspend fun setUserHeight(height: Float) {
        preferencesManager.setUserHeight(height)
    }

    suspend fun logWeightEntry(weight: Float, dateKey: String) {
        preferencesManager.logWeightEntry(weight, dateKey)
    }

    suspend fun updateDayFocus(dayOfWeek: Int, focusTitle: String) {
        withContext(Dispatchers.IO) {
            val existing = dao.getWorkoutDayOnce(dayOfWeek)
            if (existing != null) {
                dao.upsertWorkoutDay(existing.copy(focusName = focusTitle, focusSubtitle = focusTitle))
            } else {
                dao.upsertWorkoutDay(
                    WorkoutDayEntity(
                        dayOfWeek = dayOfWeek,
                        isTrainingDay = true,
                        focusName = focusTitle,
                        focusSubtitle = focusTitle
                    )
                )
            }
        }
    }

    suspend fun toggleTrainingDay(dayOfWeek: Int, isTraining: Boolean) {
        withContext(Dispatchers.IO) {
            val existing = dao.getWorkoutDayOnce(dayOfWeek)
            if (existing != null) {
                dao.upsertWorkoutDay(existing.copy(isTrainingDay = isTraining))
            } else {
                dao.upsertWorkoutDay(
                    WorkoutDayEntity(
                        dayOfWeek = dayOfWeek,
                        isTrainingDay = isTraining,
                        focusName = if (isTraining) "تمرین عمومی" else "استراحت"
                    )
                )
            }
        }
    }

    suspend fun addActivity(activity: ActivityEntity): Long {
        return withContext(Dispatchers.IO) {
            val currentList = dao.getActivitiesForDayOnce(activity.dayOfWeek)
            val nextIndex = if (currentList.isEmpty()) 0 else currentList.maxOf { it.orderIndex } + 1
            dao.insertActivity(activity.copy(orderIndex = nextIndex))
        }
    }

    suspend fun updateActivity(activity: ActivityEntity) {
        withContext(Dispatchers.IO) {
            dao.updateActivity(activity)
        }
    }

    suspend fun deleteActivity(id: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteActivityById(id)
        }
    }

    suspend fun duplicateActivity(activityId: Long): Long {
        return withContext(Dispatchers.IO) {
            val all = dao.getAllActivitiesOnce()
            val target = all.find { it.id == activityId } ?: return@withContext -1L
            val currentList = dao.getActivitiesForDayOnce(target.dayOfWeek)
            val newOrder = target.orderIndex + 1
            val duplicate = target.copy(
                id = 0,
                name = "${target.name} (کپی)",
                orderIndex = newOrder
            )
            dao.insertActivity(duplicate)
        }
    }

    suspend fun reorderActivities(dayOfWeek: Int, orderedActivities: List<ActivityEntity>) {
        withContext(Dispatchers.IO) {
            val updated = orderedActivities.mapIndexed { index, act ->
                act.copy(orderIndex = index)
            }
            dao.updateActivities(updated)
        }
    }

    suspend fun copyDayPlan(sourceDay: Int, targetDay: Int) {
        withContext(Dispatchers.IO) {
            val sourceActivities = dao.getActivitiesForDayOnce(sourceDay)
            val sourceDayInfo = dao.getWorkoutDayOnce(sourceDay)
            if (sourceDayInfo != null) {
                dao.upsertWorkoutDay(
                    sourceDayInfo.copy(
                        dayOfWeek = targetDay,
                        isTrainingDay = true
                    )
                )
            }
            dao.deleteActivitiesForDay(targetDay)
            val copied = sourceActivities.map { act ->
                act.copy(id = 0, dayOfWeek = targetDay)
            }
            dao.insertActivities(copied)
        }
    }

    suspend fun getOrCreateTodaySession(): WorkoutSessionEntity {
        return withContext(Dispatchers.IO) {
            val todayKey = PersianDateHelper.getTodayDateKey()
            val todayCal = java.util.Calendar.getInstance()
            val weekIndex = PersianDateHelper.calendarDayToWeekIndex(todayCal.get(java.util.Calendar.DAY_OF_WEEK))

            val existing = dao.getSessionForDateOnce(todayKey)
            if (existing != null) {
                return@withContext existing
            }

            val activities = dao.getActivitiesForDayOnce(weekIndex)
            val session = WorkoutSessionEntity(
                dateString = todayKey,
                dayOfWeek = weekIndex,
                isCompleted = false,
                startedAt = System.currentTimeMillis(),
                totalActivities = activities.size,
                completedActivities = 0
            )
            val id = dao.upsertSession(session)
            session.copy(id = id)
        }
    }

    suspend fun toggleActivityCompletion(
        activity: ActivityEntity,
        isCompleted: Boolean,
        actualWeight: Double? = null,
        actualReps: Int? = null,
        actualSets: Int? = null,
        actualDuration: Int? = null,
        actualDistance: Double? = null
    ) {
        withContext(Dispatchers.IO) {
            val session = getOrCreateTodaySession()
            val todayKey = session.dateString

            if (isCompleted) {
                val completedRecord = CompletedActivityEntity(
                    sessionId = session.id,
                    activityId = activity.id,
                    dateString = todayKey,
                    activityName = activity.name,
                    activityType = activity.type,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    actualWeight = actualWeight ?: activity.weight,
                    actualSets = actualSets ?: activity.sets,
                    actualReps = actualReps ?: activity.reps,
                    actualDurationMinutes = actualDuration ?: activity.durationMinutes,
                    actualDistanceKm = actualDistance ?: activity.distanceKm
                )
                dao.upsertCompletedActivity(completedRecord)
            } else {
                dao.deleteCompletedActivity(session.id, activity.id)
            }

            // Recalculate session progress
            val allCompletedToday = dao.getCompletedActivitiesForDateOnce(todayKey)
            val totalActs = dao.getActivitiesForDayOnce(session.dayOfWeek).size
            val isAllDone = allCompletedToday.size >= totalActs && totalActs > 0

            val updatedSession = session.copy(
                totalActivities = totalActs,
                completedActivities = allCompletedToday.size,
                isCompleted = isAllDone,
                completedAt = if (isAllDone) System.currentTimeMillis() else null
            )
            dao.updateSession(updatedSession)
        }
    }

    suspend fun toggleActivityCompletionForDate(
        activity: ActivityEntity,
        dayOfWeek: Int,
        dateKey: String,
        isCompleted: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val session = dao.getSessionForDateOnce(dateKey) ?: run {
                val newSession = WorkoutSessionEntity(
                    dateString = dateKey,
                    dayOfWeek = dayOfWeek,
                    isCompleted = false,
                    totalActivities = dao.getActivitiesForDayOnce(dayOfWeek).size
                )
                val sid = dao.upsertSession(newSession)
                newSession.copy(id = sid)
            }

            if (isCompleted) {
                val completedRecord = CompletedActivityEntity(
                    sessionId = session.id,
                    activityId = activity.id,
                    dateString = dateKey,
                    activityName = activity.name,
                    activityType = activity.type,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    actualWeight = activity.weight,
                    actualSets = activity.sets,
                    actualReps = activity.reps,
                    actualDurationMinutes = activity.durationMinutes,
                    actualDistanceKm = activity.distanceKm
                )
                dao.upsertCompletedActivity(completedRecord)
            } else {
                dao.deleteCompletedActivity(session.id, activity.id)
            }

            val allCompleted = dao.getCompletedActivitiesForDateOnce(dateKey)
            val totalActs = dao.getActivitiesForDayOnce(dayOfWeek).size
            val isAllDone = allCompleted.size >= totalActs && totalActs > 0
            dao.updateSession(
                session.copy(
                    totalActivities = totalActs,
                    completedActivities = allCompleted.size,
                    isCompleted = isAllDone,
                    completedAt = if (isAllDone) System.currentTimeMillis() else null
                )
            )
        }
    }

    suspend fun finishTodayWorkout() {
        withContext(Dispatchers.IO) {
            val session = getOrCreateTodaySession()
            val todayKey = session.dateString
            val activities = dao.getActivitiesForDayOnce(session.dayOfWeek)

            // Mark any remaining activities as completed
            val currentCompleted = dao.getCompletedActivitiesForDateOnce(todayKey)
            val completedIds = currentCompleted.map { it.activityId }.toSet()

            for (act in activities) {
                if (act.id !in completedIds) {
                    dao.upsertCompletedActivity(
                        CompletedActivityEntity(
                            sessionId = session.id,
                            activityId = act.id,
                            dateString = todayKey,
                            activityName = act.name,
                            activityType = act.type,
                            isCompleted = true,
                            completedAt = System.currentTimeMillis(),
                            actualWeight = act.weight,
                            actualSets = act.sets,
                            actualReps = act.reps,
                            actualDurationMinutes = act.durationMinutes,
                            actualDistanceKm = act.distanceKm
                        )
                    )
                }
            }

            dao.updateSession(
                session.copy(
                    isCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    totalActivities = activities.size,
                    completedActivities = activities.size
                )
            )
        }
    }

    suspend fun getPreviousRecord(activityName: String): String? {
        return withContext(Dispatchers.IO) {
            val record = dao.getLatestCompletedRecord(activityName) ?: return@withContext null
            when (record.activityType) {
                ActivityType.WEIGHT_REPS -> "${record.actualWeight} kg × ${record.actualReps}"
                ActivityType.DURATION -> "${record.actualDurationMinutes} min"
                ActivityType.DISTANCE -> "${record.actualDistanceKm} km"
                ActivityType.SIMPLE -> "انجام شده"
            }
        }
    }

    suspend fun exportBackupJson(): String {
        return withContext(Dispatchers.IO) {
            val profile = dao.getUserProfileOnce()
            val days = dao.getAllWorkoutDaysOnce()
            val activities = dao.getAllActivitiesOnce()
            val sessions = dao.getAllSessionsOnce()
            val completed = dao.getAllCompletedActivitiesOnce()
            val prefs = preferencesFlow.first()

            val root = JSONObject()
            root.put("version", 1)
            root.put("timestamp", System.currentTimeMillis())

            profile?.let { p ->
                val pObj = JSONObject()
                pObj.put("name", p.name)
                pObj.put("age", p.age ?: -1)
                pObj.put("hasCompletedOnboarding", p.hasCompletedOnboarding)
                root.put("profile", pObj)
            }

            val daysArr = JSONArray()
            days.forEach { d ->
                val dObj = JSONObject()
                dObj.put("dayOfWeek", d.dayOfWeek)
                dObj.put("isTrainingDay", d.isTrainingDay)
                dObj.put("focusName", d.focusName)
                dObj.put("focusSubtitle", d.focusSubtitle)
                dObj.put("estimatedDurationMinutes", d.estimatedDurationMinutes)
                daysArr.put(dObj)
            }
            root.put("workoutDays", daysArr)

            val actArr = JSONArray()
            activities.forEach { a ->
                val aObj = JSONObject()
                aObj.put("dayOfWeek", a.dayOfWeek)
                aObj.put("name", a.name)
                aObj.put("subtitle", a.subtitle)
                aObj.put("type", a.type.name)
                aObj.put("orderIndex", a.orderIndex)
                aObj.put("weight", a.weight)
                aObj.put("sets", a.sets)
                aObj.put("reps", a.reps)
                aObj.put("durationMinutes", a.durationMinutes)
                aObj.put("distanceKm", a.distanceKm)
                aObj.put("notes", a.notes)
                actArr.put(aObj)
            }
            root.put("activities", actArr)

            val sessArr = JSONArray()
            sessions.forEach { s ->
                val sObj = JSONObject()
                sObj.put("dateString", s.dateString)
                sObj.put("dayOfWeek", s.dayOfWeek)
                sObj.put("isCompleted", s.isCompleted)
                sObj.put("startedAt", s.startedAt)
                sObj.put("completedAt", s.completedAt ?: -1L)
                sObj.put("totalActivities", s.totalActivities)
                sObj.put("completedActivities", s.completedActivities)
                sessArr.put(sObj)
            }
            root.put("sessions", sessArr)

            val compArr = JSONArray()
            completed.forEach { c ->
                val cObj = JSONObject()
                cObj.put("sessionId", c.sessionId)
                cObj.put("activityId", c.activityId)
                cObj.put("dateString", c.dateString)
                cObj.put("activityName", c.activityName)
                cObj.put("activityType", c.activityType.name)
                cObj.put("isCompleted", c.isCompleted)
                cObj.put("completedAt", c.completedAt)
                cObj.put("actualWeight", c.actualWeight)
                cObj.put("actualSets", c.actualSets)
                cObj.put("actualReps", c.actualReps)
                cObj.put("actualDurationMinutes", c.actualDurationMinutes)
                cObj.put("actualDistanceKm", c.actualDistanceKm)
                compArr.put(cObj)
            }
            root.put("completedActivities", compArr)

            val prefsObj = JSONObject()
            prefsObj.put("language", prefs.language.code)
            prefsObj.put("themeMode", prefs.themeMode.name)
            prefsObj.put("weightUnit", prefs.weightUnit.name)
            prefsObj.put("reminder", prefs.workoutReminderEnabled)
            root.put("preferences", prefsObj)

            val jsonStr = root.toString(2)
            val sizeKb = (jsonStr.toByteArray().size / 1024).coerceAtLeast(1)
            preferencesManager.updateBackupInfo(System.currentTimeMillis(), sizeKb)
            jsonStr
        }
    }

    suspend fun importBackupJson(jsonString: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val root = JSONObject(jsonString)
                if (!root.has("workoutDays") || !root.has("activities")) {
                    return@withContext Result.failure(IllegalArgumentException("Invalid Fit Plan backup"))
                }

                if (root.has("profile")) {
                    val pObj = root.getJSONObject("profile")
                    val name = pObj.optString("name", "نیما")
                    val age = if (pObj.has("age") && pObj.getInt("age") > 0) pObj.getInt("age") else null
                    val completed = pObj.optBoolean("hasCompletedOnboarding", true)
                    dao.upsertUserProfile(UserProfileEntity(id = 1, name = name, age = age, hasCompletedOnboarding = completed))
                }

                // Days
                val daysArr = root.getJSONArray("workoutDays")
                val newDays = mutableListOf<WorkoutDayEntity>()
                for (i in 0 until daysArr.length()) {
                    val dObj = daysArr.getJSONObject(i)
                    newDays.add(
                        WorkoutDayEntity(
                            dayOfWeek = dObj.getInt("dayOfWeek"),
                            isTrainingDay = dObj.optBoolean("isTrainingDay", false),
                            focusName = dObj.optString("focusName", ""),
                            focusSubtitle = dObj.optString("focusSubtitle", ""),
                            estimatedDurationMinutes = dObj.optInt("estimatedDurationMinutes", 45)
                        )
                    )
                }

                // Activities
                val actArr = root.getJSONArray("activities")
                val newActs = mutableListOf<ActivityEntity>()
                for (i in 0 until actArr.length()) {
                    val aObj = actArr.getJSONObject(i)
                    val type = try {
                        ActivityType.valueOf(aObj.getString("type"))
                    } catch (_: Exception) {
                        ActivityType.WEIGHT_REPS
                    }
                    newActs.add(
                        ActivityEntity(
                            dayOfWeek = aObj.getInt("dayOfWeek"),
                            name = aObj.getString("name"),
                            subtitle = aObj.optString("subtitle", ""),
                            type = type,
                            orderIndex = aObj.optInt("orderIndex", i),
                            weight = aObj.optDouble("weight", 0.0),
                            sets = aObj.optInt("sets", 3),
                            reps = aObj.optInt("reps", 10),
                            durationMinutes = aObj.optInt("durationMinutes", 15),
                            distanceKm = aObj.optDouble("distanceKm", 0.0),
                            notes = aObj.optString("notes", "")
                        )
                    )
                }

                // Atomic replace
                dao.clearAllActivities()
                dao.clearAllWorkoutDays()
                dao.upsertWorkoutDays(newDays)
                dao.insertActivities(newActs)

                // Optional preferences
                if (root.has("preferences")) {
                    val prefsObj = root.getJSONObject("preferences")
                    val lang = if (prefsObj.optString("language") == "en") AppLanguage.ENGLISH else AppLanguage.PERSIAN
                    preferencesManager.setLanguage(lang)
                    val theme = try {
                        ThemeMode.valueOf(prefsObj.optString("themeMode", ThemeMode.DARK.name))
                    } catch (_: Exception) {
                        ThemeMode.DARK
                    }
                    preferencesManager.setThemeMode(theme)
                    val unit = try {
                        WeightUnit.valueOf(prefsObj.optString("weightUnit", WeightUnit.KG.name))
                    } catch (_: Exception) {
                        WeightUnit.KG
                    }
                    preferencesManager.setWeightUnit(unit)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun resetToFreshInstall() {
        withContext(Dispatchers.IO) {
            dao.clearAllActivities()
            dao.clearAllSessions()
            dao.clearAllCompletedActivities()
            dao.clearAllWorkoutDays()
            dao.clearUserProfile()
            preferencesManager.resetToFreshInstall()

            dao.upsertUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = "",
                    age = null,
                    hasCompletedOnboarding = false
                )
            )

            val cleanDays = (1..7).map { dayIndex ->
                WorkoutDayEntity(
                    dayOfWeek = dayIndex,
                    isTrainingDay = false,
                    focusName = "",
                    focusSubtitle = "",
                    estimatedDurationMinutes = 45
                )
            }
            dao.upsertWorkoutDays(cleanDays)
        }
    }

    suspend fun preSeedDefaultDataIfEmpty() {
        withContext(Dispatchers.IO) {
            val existingProfile = dao.getUserProfileOnce()
            if (existingProfile == null) {
                dao.upsertUserProfile(
                    UserProfileEntity(
                        id = 1,
                        name = "",
                        age = null,
                        hasCompletedOnboarding = false
                    )
                )
            }

            val existingDays = dao.getAllWorkoutDaysOnce()
            if (existingDays.isEmpty()) {
                val cleanDays = (1..7).map { dayIndex ->
                    WorkoutDayEntity(
                        dayOfWeek = dayIndex,
                        isTrainingDay = false,
                        focusName = "",
                        focusSubtitle = "",
                        estimatedDurationMinutes = 45
                    )
                }
                dao.upsertWorkoutDays(cleanDays)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FitPlanRepository? = null

        fun getInstance(context: Context): FitPlanRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val prefs = PreferencesManager(context)
                val repo = FitPlanRepository(db, prefs)
                INSTANCE = repo
                repo
            }
        }
    }
}
